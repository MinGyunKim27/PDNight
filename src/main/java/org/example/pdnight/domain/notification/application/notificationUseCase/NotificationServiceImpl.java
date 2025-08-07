package org.example.pdnight.domain.notification.application.notificationUseCase;


import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.notification.presentation.dto.NotificationResponse;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationCommanderService commanderService;
    private final NotificationReaderService readerService;

    @Override
    public void isReadCheck(Long id, Long userid) {
        commanderService.isReadCheck(id, userid);
    }

    @Override
    public PagedResponse<NotificationResponse> findAllNotification(Long userid, Pageable pageable) {
        return readerService.findAllNotification(userid, pageable);
    }

    @Override
    public PagedResponse<NotificationResponse> findIsReadFalseNotification(Long userid, Pageable pageable) {
        return readerService.findIsReadFalseNotification(userid, pageable);
    }
}
