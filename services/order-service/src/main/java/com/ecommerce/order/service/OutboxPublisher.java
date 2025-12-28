package com.ecommerce.order.service;

import com.ecommerce.order.entity.OutboxEvent;
import com.ecommerce.order.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishEvents() {
        List<OutboxEvent> pendingEvents = outboxRepository.findByStatusOrderByCreatedAtAsc("PENDING");

        for (OutboxEvent event : pendingEvents) {
            try {
                log.info("Publishing event: {} for aggregate: {}", event.getEventType(), event.getAggregateId());

                // Determine topic based on aggregate type or event type
                String topic = getTopicForEvent(event);

                // Deserialize payload to send as object (JsonSerializer will handle it)
                Object payload = objectMapper.readValue(event.getPayload(), Object.class);

                kafkaTemplate.send(topic, event.getAggregateId(), payload)
                        .whenComplete((result, ex) -> {
                            if (ex == null) {
                                updateEventStatus(event, "PROCESSED");
                            } else {
                                log.error("Failed to publish event: {}", event.getId(), ex);
                                updateEventStatus(event, "FAILED");
                            }
                        });

            } catch (Exception e) {
                log.error("Error processing outbox event: {}", event.getId(), e);
                event.setStatus("FAILED");
                outboxRepository.save(event);
            }
        }
    }

    private void updateEventStatus(OutboxEvent event, String status) {
        event.setStatus(status);
        event.setProcessedAt(LocalDateTime.now());
        outboxRepository.save(event);
    }

    private String getTopicForEvent(OutboxEvent event) {
        switch (event.getAggregateType()) {
            case "ORDER":
                return "orders.created";
            default:
                return "misc.events";
        }
    }
}
