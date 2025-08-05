package org.example.pdnight.domain.notification.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.notification.domain.Notification;
import org.example.pdnight.domain.notification.domain.NotificationCommander;
import org.example.pdnight.domain.notification.domain.NotificationReader;
import org.example.pdnight.domain.notification.enums.NotificationType;
import org.example.pdnight.domain.notification.presentation.NotificationSocketSender;
import org.example.pdnight.domain.notification.presentation.dto.NotificationResponse;
import org.example.pdnight.domain.notification.presentation.dto.event.PostConfirmedEvent;
import org.example.pdnight.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumeService {
    private final NotificationReader notificationReader;
    private final NotificationCommander notificationCommander;
    private final NotificationSocketSender webSocketSender;

    public void isReadCheck(Long id, Long userid) {
        Notification notification = notificationReader.findByIdIsReadFalse(id);
        if (userid.equals(notification.getReceiver())) {
            notification.markAsRead();
        } else {
            throw new BaseException(HttpStatus.BAD_REQUEST, "본인이 아닙니다!");
        }
        notificationCommander.save(notification);
    }

    @KafkaListener(topics = "post.participant.confirmed", groupId = "alert-group")
    public void handlePostConfirmed(PostConfirmedEvent event) {

        // 참가자들에게 전송
        for (Long userId : event.confirmedUserIds()) {
            Notification notification = Notification.from(null, userId, "모임이 확정되었습니다!", event.authorId(), NotificationType.POST_CONFIRMED);
            NotificationResponse dto = NotificationResponse.from(notification);
            notificationCommander.save(notification);
            log.info("알림 저장 완료");
            webSocketSender.sendToUser(userId, dto);
        }

        // 작성자에게도 전송
        Notification notification = Notification.from(null, event.authorId(), "모임이 확정되었습니다!", event.authorId(), NotificationType.POST_CONFIRMED);
        NotificationResponse dto = NotificationResponse.from(notification);
        notificationCommander.save(notification);
        log.info("알림 저장 완료");
        webSocketSender.sendToUser(event.authorId(), dto);

    }
}
