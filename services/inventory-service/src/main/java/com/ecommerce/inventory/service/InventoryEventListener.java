package com.ecommerce.inventory.service;

import com.ecommerce.inventory.entity.Inventory;
import com.ecommerce.inventory.repository.InventoryRepository;
import com.ecommerce.shared.event.inventory.InventoryReservedEvent;
import com.ecommerce.shared.event.inventory.InventoryReservationFailedEvent;
import com.ecommerce.shared.event.order.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryEventListener {

    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "orders.created", groupId = "inventory-service-group")
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Processing inventory for order: {}", event.getOrderId());

        boolean allReserved = true;
        try {
            for (OrderCreatedEvent.OrderItemEvent item : event.getItems()) {
                Inventory inventory = inventoryRepository.findByProductId(item.getProductId())
                        .orElseThrow(
                                () -> new RuntimeException("Product not found in inventory: " + item.getProductId()));

                if (inventory.getQuantity() < item.getQuantity()) {
                    allReserved = false;
                    break;
                }

                inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
                inventoryRepository.save(inventory);
            }

            if (allReserved) {
                log.info("All items reserved for order: {}", event.getOrderId());
                InventoryReservedEvent reservedEvent = InventoryReservedEvent.builder()
                        .orderId(event.getOrderId())
                        .allocationId(UUID.randomUUID())
                        .status("SUCCESS")
                        .timestamp(LocalDateTime.now())
                        .build();
                kafkaTemplate.send("inventory.reserved", event.getOrderId().toString(), reservedEvent);
            } else {
                log.warn("Insufficient stock for order: {}", event.getOrderId());
                emitFailedEvent(event.getOrderId(), "INSUFFICIENT_STOCK");
            }

        } catch (Exception e) {
            log.error("Error processing inventory for order: {}", event.getOrderId(), e);
            emitFailedEvent(event.getOrderId(), e.getMessage());
        }
    }

    private void emitFailedEvent(UUID orderId, String reason) {
        InventoryReservationFailedEvent failedEvent = InventoryReservationFailedEvent.builder()
                .orderId(orderId)
                .reason(reason)
                .timestamp(LocalDateTime.now())
                .build();
        kafkaTemplate.send("inventory.failed", orderId.toString(), failedEvent);
    }
}
