package org.example.pdnight.domain.notification.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.notification.presentation.dto.NotificationResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSocketSender {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendToUser(Long receiverId, NotificationResponse response) {
        String destination = "/sub/notifications/" + receiverId;
        messagingTemplate.convertAndSend(destination, response);
        log.info("WebSocket 알림 전송 완료 to user {}", response.getReceiverId());
    }
}