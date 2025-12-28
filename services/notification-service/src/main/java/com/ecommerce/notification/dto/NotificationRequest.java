package com.ecommerce.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private UUID userId;
    private String notificationType;
    private String channel;
    private String recipient;
    private String subject;
    private String content;
}
