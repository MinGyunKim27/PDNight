package org.example.pdnight.domain.notification.application.notificationUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.notification.domain.Notification;
import org.example.pdnight.domain.notification.domain.NotificationCommander;
import org.example.pdnight.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationCommanderService {

    private final NotificationCommander notificationCommander;

    // 알림 확인 체크 메서드
    public void isReadCheck(Long id, Long userid) {
        Notification notification = notificationCommander.findByIdIsReadFalse(id)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다!"));

        if (!userid.equals(notification.getReceiverId())) {
            throw new BaseException(HttpStatus.BAD_REQUEST, "본인이 아닙니다!");
        }
        notification.markAsRead();

        notificationCommander.save(notification);
    }

}
