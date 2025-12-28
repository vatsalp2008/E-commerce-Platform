package com.ecommerce.payment.service;

import com.ecommerce.shared.event.inventory.InventoryReservedEvent;
import com.ecommerce.shared.event.payment.PaymentProcessedEvent;
import com.ecommerce.shared.event.payment.PaymentFailedEvent;
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
public class PaymentEventListener {

    private final PaymentService paymentService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "inventory.reserved", groupId = "payment-service-group")
    @Transactional
    public void handleInventoryReserved(InventoryReservedEvent event) {
        log.info("Processing payment for order: {}", event.getOrderId());

        try {
            // Mock payment processing (reusing existing logic from Phase 1)
            // In a real system, we'd fetch the order details or the payment request
            // For now, we simulate a process.

            boolean success = Math.random() < 0.9; // 90% success rate

            if (success) {
                String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                log.info("Payment successful for order: {}. Transaction: {}", event.getOrderId(), transactionId);

                PaymentProcessedEvent processedEvent = PaymentProcessedEvent.builder()
                        .orderId(event.getOrderId())
                        .transactionId(transactionId)
                        .status("COMPLETED")
                        .timestamp(LocalDateTime.now())
                        .build();

                kafkaTemplate.send("payments.processed", event.getOrderId().toString(), processedEvent);
            } else {
                log.warn("Payment failed for order: {}", event.getOrderId());
                PaymentFailedEvent failedEvent = PaymentFailedEvent.builder()
                        .orderId(event.getOrderId())
                        .reason("INSUFFICIENT_FUNDS")
                        .timestamp(LocalDateTime.now())
                        .build();

                kafkaTemplate.send("payments.failed", event.getOrderId().toString(), failedEvent);
            }

        } catch (Exception e) {
            log.error("Error processing payment for order: {}", event.getOrderId(), e);
            PaymentFailedEvent failedEvent = PaymentFailedEvent.builder()
                    .orderId(event.getOrderId())
                    .reason(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
            kafkaTemplate.send("payments.failed", event.getOrderId().toString(), failedEvent);
        }
    }
}
