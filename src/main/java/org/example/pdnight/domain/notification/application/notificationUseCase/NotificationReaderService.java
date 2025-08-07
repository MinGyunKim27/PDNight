package org.example.pdnight.domain.notification.application.notificationUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.notification.domain.Notification;
import org.example.pdnight.domain.notification.domain.NotificationReader;
import org.example.pdnight.domain.notification.presentation.dto.NotificationResponse;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationReaderService {

    private final NotificationReader notificationReader;

    // 사용자의 모든 알림 조회
    public PagedResponse<NotificationResponse> findAllNotification(Long userId, Pageable pageable) {
        Page<Notification> notificationPage = notificationReader.findAll(userId, pageable);
        return PagedResponse.from(notificationPage.map(NotificationResponse::from));
    }

    //사용자의 읽지 않은 알림 조회
    public PagedResponse<NotificationResponse> findIsReadFalseNotification(Long userId, Pageable pageable) {
        Page<Notification> notificationPage = notificationReader.findIsReadFalse(userId, pageable);
        return PagedResponse.from(notificationPage.map(NotificationResponse::from));
    }
}
