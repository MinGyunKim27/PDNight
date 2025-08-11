package org.example.pdnight.domain.notification.application.notificationUseCase;

import org.example.pdnight.domain.notification.domain.Notification;
import org.example.pdnight.domain.notification.domain.NotificationReader;
import org.example.pdnight.domain.notification.presentation.dto.NotificationResponse;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationReaderServiceTest {

    @InjectMocks
    private NotificationReaderService notificationReaderService;

    @Mock
    private NotificationReader notificationReader;

    @Test
    @DisplayName("사용자의 모든 알림 조회 성공 테스트")
    void findAllNotification() {
        // given
        Long userId = 1L;
        Long notificationId = 1L;
        String content = "content";
        boolean isRead = false;

        Notification notification = mock(Notification.class);
        when(notification.getReceiverId()).thenReturn(userId);
        when(notification.getId()).thenReturn(notificationId);
        when(notification.getContent()).thenReturn(content);
        when(notification.getIsRead()).thenReturn(isRead);

        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Notification> notificationPage = new PageImpl<>(List.of(notification), pageable, 1);

        // when
        when(notificationReader.findAll(userId, pageable)).thenReturn(notificationPage);
        PagedResponse<NotificationResponse> response = notificationReaderService.findAllNotification(userId, pageable);

        // then
        assertNotNull(response);
        assertEquals(1, response.contents().size());
        assertEquals(userId, response.contents().get(0).getReceiverId());
        assertEquals(notificationId, response.contents().get(0).getNotificationId());
        assertEquals(content, response.contents().get(0).getContent());
        assertEquals(isRead, response.contents().get(0).isRead());

        verify(notificationReader).findAll(userId, pageable);
    }

    @Test
    @DisplayName("사용자의 모든 알림 조회 성공 테스트")
    void findIsReadFalseNotification() {
        // given
        Long userId = 1L;
        Long notificationId = 1L;
        String content = "content";
        boolean isRead = false;

        Notification notification = mock(Notification.class);
        when(notification.getReceiverId()).thenReturn(userId);
        when(notification.getId()).thenReturn(notificationId);
        when(notification.getContent()).thenReturn(content);
        when(notification.getIsRead()).thenReturn(isRead);

        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Notification> notificationPage = new PageImpl<>(List.of(notification), pageable, 1);

        // when
        when(notificationReader.findIsReadFalse(userId, pageable)).thenReturn(notificationPage);
        PagedResponse<NotificationResponse> response = notificationReaderService.findIsReadFalseNotification(userId, pageable);

        // then
        assertNotNull(response);
        assertEquals(1, response.contents().size());
        assertEquals(userId, response.contents().get(0).getReceiverId());
        assertEquals(notificationId, response.contents().get(0).getNotificationId());
        assertEquals(content, response.contents().get(0).getContent());
        assertEquals(isRead, response.contents().get(0).isRead());

        verify(notificationReader).findIsReadFalse(userId, pageable);
    }
}
