package org.example.pdnight.domain.notification.presentation.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.pdnight.domain.notification.enums.NotificationType;
import org.example.pdnight.domain.notification.domain.Notification;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {

    private Long notificationId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private NotificationType type;
    private boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getId())
                .senderId(notification.getSender())
                .receiverId(notification.getReceiver())
                .content(notification.getContent())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
