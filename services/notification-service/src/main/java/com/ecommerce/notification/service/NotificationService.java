package com.ecommerce.notification.service;

import com.ecommerce.notification.dto.NotificationRequest;
import com.ecommerce.notification.entity.Notification;
import com.ecommerce.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void sendNotification(NotificationRequest request) {
        log.info("Sending notification of type {} to {}", request.getNotificationType(), request.getRecipient());

        // Mock sending logic
        log.info("MOCK [{}] SENT TO {}: Subject: {}, Content: {}",
                request.getChannel(), request.getRecipient(), request.getSubject(), request.getContent());

        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .notificationType(request.getNotificationType())
                .channel(request.getChannel())
                .recipient(request.getRecipient())
                .subject(request.getSubject())
                .content(request.getContent())
                .status("SENT")
                .sentAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
