package org.example.pdnight.domain.notification.application.notificationUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.notification.domain.Notification;
import org.example.pdnight.domain.notification.domain.NotificationCommander;
import org.example.pdnight.domain.notification.enums.NotificationType;
import org.example.pdnight.domain.notification.infra.NotificationSocketSender;
import org.example.pdnight.domain.notification.presentation.dto.NotificationResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumerService {

    private final NotificationCommander notificationCommander;
    private final NotificationSocketSender webSocketSender;

    // 하나의 유저에게 알림 보낼 때 사용하는 메서드
    public void one(Long receiverId, Long senderId, String message, String logMessage, NotificationType type) {
        sendMessage(receiverId, senderId, message, logMessage, type);
    }

    // 복수의 유저에게 알림 보낼 때 사용하는 메서드
    public void many(List<Long> receiverIds, Long senderId, String message, String logMessage, NotificationType type) {
        for(Long receiverId : receiverIds) {
            sendMessage(receiverId, senderId, message, logMessage, type);
        }
    }

    // 알림 send 메서드
    private void sendMessage(Long receiverId, Long senderId, String message, String logMessage, NotificationType type) {
        Notification notification = Notification.from(receiverId, message, senderId, type);
        NotificationResponse dto = NotificationResponse.from(notification);
        notificationCommander.save(notification);
        log.info(logMessage + "userId : {}", receiverId);
        webSocketSender.sendToUser(receiverId, dto);
    }

}