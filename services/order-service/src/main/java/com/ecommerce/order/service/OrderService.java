package com.ecommerce.order.service;

import com.ecommerce.order.dto.CreateOrderRequest;
import com.ecommerce.order.dto.OrderDTO;
import com.ecommerce.order.dto.OrderItemDTO;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.exception.ResourceNotFoundException;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.repository.OutboxRepository;
import com.ecommerce.order.entity.OutboxEvent;
import com.ecommerce.shared.event.order.OrderCreatedEvent;
import com.ecommerce.shared.event.inventory.InventoryReservedEvent;
import com.ecommerce.shared.event.inventory.InventoryReservationFailedEvent;
import com.ecommerce.shared.event.payment.PaymentProcessedEvent;
import com.ecommerce.shared.event.payment.PaymentFailedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${application.services.cart-url}")
    private String cartUrl;

    @Value("${application.services.payment-url}")
    private String paymentUrl;

    @Value("${application.services.inventory-url}")
    private String inventoryUrl;

    @Transactional
    public OrderDTO createOrder(UUID userId, CreateOrderRequest request) {
        log.info("Creating order for user: {}", userId);

        // 1. Get Cart
        Map<String, Object> cart = fetchCart(userId);
        List<Map<String, Object>> cartItems = (List<Map<String, Object>>) cart.get("items");
        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Cannot create order from empty cart");
        }

        BigDecimal totalAmount = new BigDecimal(cart.get("totalAmount").toString());

        // 2. Generate Order Number
        String orderNumber = generateOrderNumber();

        // 3. Create Order Entity
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .userId(userId)
                .status("PENDING")
                .totalAmount(totalAmount)
                .shippingAddressId(request.getShippingAddressId())
                .billingAddressId(request.getBillingAddressId())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus("PENDING")
                .items(new ArrayList<>())
                .build();

        // 4. Map Cart Items to Order Items
        for (Map<String, Object> ci : cartItems) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(UUID.fromString(ci.get("productId").toString()))
                    .productName(ci.get("productName").toString())
                    .quantity(Integer.parseInt(ci.get("quantity").toString()))
                    .unitPrice(new BigDecimal(ci.get("productPrice").toString()))
                    .totalPrice(new BigDecimal(ci.get("productPrice").toString())
                            .multiply(BigDecimal.valueOf(Long.parseLong(ci.get("quantity").toString()))))
                    .build();
            order.getItems().add(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        // 5. Save to Outbox for Event-Driven Processing
        saveOrderCreatedEventToOutbox(savedOrder);

        // 6. Clear Cart (Still synchronous for now as it's a side effect for the user)
        clearCart(userId);

        log.info("Order created (PENDING): {}", savedOrder.getOrderNumber());
        return mapToDTO(savedOrder);
    }

    private void saveOrderCreatedEventToOutbox(Order order) {
        try {
            OrderCreatedEvent event = OrderCreatedEvent.builder()
                    .orderId(order.getId())
                    .userId(order.getUserId())
                    .totalAmount(order.getTotalAmount())
                    .timestamp(LocalDateTime.now())
                    .items(order.getItems().stream()
                            .map(item -> OrderCreatedEvent.OrderItemEvent.builder()
                                    .productId(item.getProductId())
                                    .quantity(item.getQuantity())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateType("ORDER")
                    .aggregateId(order.getId().toString())
                    .eventType("OrderCreated")
                    .payload(objectMapper.writeValueAsString(event))
                    .status("PENDING")
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxRepository.save(outboxEvent);
        } catch (Exception e) {
            log.error("Failed to save OrderCreatedEvent to outbox", e);
            throw new RuntimeException("Could not initiate order processing", e);
        }
    }

    public List<OrderDTO> getMyOrders(UUID userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderById(UUID id) {
        return orderRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    @Transactional
    public void cancelOrder(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (!order.getStatus().equals("PENDING") && !order.getStatus().equals("CONFIRMED")) {
            throw new RuntimeException("Cannot cancel order in status: " + order.getStatus());
        }

        order.setStatus("CANCELLED");
        orderRepository.save(order);

        // Release inventory via event (Phase 2)
        // saveOrderCancelledEventToOutbox(order);
    }

    @KafkaListener(topics = "inventory.reserved", groupId = "order-service-group")
    @Transactional
    public void handleInventoryReserved(InventoryReservedEvent event) {
        log.info("Inventory reserved for order: {}", event.getOrderId());
        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setStatus("INVENTORY_RESERVED");
            orderRepository.save(order);
        });
    }

    @KafkaListener(topics = "inventory.failed", groupId = "order-service-group")
    @Transactional
    public void handleInventoryFailed(InventoryReservationFailedEvent event) {
        log.error("Inventory reservation failed for order: {}. Reason: {}", event.getOrderId(), event.getReason());
        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setStatus("FAILED_INVENTORY");
            orderRepository.save(order);
        });
    }

    @KafkaListener(topics = "payments.processed", groupId = "order-service-group")
    @Transactional
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        log.info("Payment processed for order: {}. Status: {}", event.getOrderId(), event.getStatus());
        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setPaymentStatus(event.getStatus());
            if (event.getStatus().equals("COMPLETED")) {
                order.setStatus("CONFIRMED");
            } else {
                order.setStatus("FAILED_PAYMENT");
            }
            orderRepository.save(order);
        });
    }

    @KafkaListener(topics = "payments.failed", groupId = "order-service-group")
    @Transactional
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.error("Payment failed for order: {}. Reason: {}", event.getOrderId(), event.getReason());
        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setPaymentStatus("FAILED");
            order.setStatus("FAILED_PAYMENT");
            orderRepository.save(order);
        });
    }

    private String generateOrderNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.format("%03d", new Random().nextInt(1000));
        return "ORD-" + datePart + "-" + randomPart;
    }

    private Map<String, Object> fetchCart(UUID userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", userId.toString());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(cartUrl + "/api/cart", HttpMethod.GET, entity, Map.class).getBody();
    }

    private void reserveInventory(Order order) {
        for (OrderItem item : order.getItems()) {
            Map<String, Object> request = new HashMap<>();
            request.put("productId", item.getProductId());
            request.put("quantity", item.getQuantity());
            request.put("referenceId", order.getId());
            restTemplate.postForLocation(inventoryUrl + "/api/inventory/reserve", request);
        }
    }

    private void releaseInventory(Order order) {
        for (OrderItem item : order.getItems()) {
            Map<String, Object> request = new HashMap<>();
            request.put("productId", item.getProductId());
            request.put("quantity", item.getQuantity());
            request.put("referenceId", order.getId());
            restTemplate.postForLocation(inventoryUrl + "/api/inventory/release", request);
        }
    }

    private void processPayment(Order order) {
        Map<String, Object> request = new HashMap<>();
        request.put("orderId", order.getId());
        request.put("amount", order.getTotalAmount());
        request.put("currency", "USD");
        request.put("paymentMethod", order.getPaymentMethod());

        try {
            Map<String, Object> response = restTemplate.postForObject(paymentUrl + "/api/payments/process", request,
                    Map.class);
            String status = response.get("status").toString();
            order.setPaymentStatus(status);
            if (status.equals("COMPLETED")) {
                order.setStatus("CONFIRMED");
            } else {
                order.setStatus("FAILED");
            }
            orderRepository.save(order);
        } catch (Exception e) {
            log.error("Payment failed for order: {}", order.getOrderNumber());
            order.setPaymentStatus("FAILED");
            order.setStatus("FAILED");
            orderRepository.save(order);
        }
    }

    private void clearCart(UUID userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", userId.toString());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        restTemplate.exchange(cartUrl + "/api/cart", HttpMethod.DELETE, entity, Void.class);
    }

    private OrderDTO mapToDTO(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .shippingAddressId(order.getShippingAddressId())
                .billingAddressId(order.getBillingAddressId())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .items(order.getItems().stream()
                        .map(item -> OrderItemDTO.builder()
                                .id(item.getId())
                                .productId(item.getProductId())
                                .productName(item.getProductName())
                                .quantity(item.getQuantity())
                                .unitPrice(item.getUnitPrice())
                                .totalPrice(item.getTotalPrice())
                                .build())
                        .collect(Collectors.toList()))
                .createdAt(order.getCreatedAt().toString())
                .build();
    }
}
