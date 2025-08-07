package org.example.pdnight.domain.notification.application.notificationUseCase;

import org.example.pdnight.domain.notification.presentation.dto.NotificationResponse;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    void isReadCheck(Long id, Long userid);

    PagedResponse<NotificationResponse> findAllNotification(Long userid, Pageable pageable);

    PagedResponse<NotificationResponse> findIsReadFalseNotification(Long userid, Pageable pageable);
}
