package org.example.pdnight.domain.notification.application.notificationUseCase;

import org.example.pdnight.domain.notification.domain.Notification;
import org.example.pdnight.domain.notification.domain.NotificationCommander;
import org.example.pdnight.global.common.exception.BaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationCommanderServiceTest {

    @InjectMocks
    private NotificationCommanderService notificationCommanderService;

    @Mock
    private NotificationCommander notificationCommander;

    @Test
    @DisplayName("알림 읽기 성공 테스트")
    void isReadCheck() {
        // given
        Long id = 1L;
        Long userId = 1L;
        Notification notification = mock(Notification.class);

        // when
        // 알림 찾기
        when(notificationCommander.findByIdIsReadFalse(id)).thenReturn(Optional.of(notification));

        // 본인것인지 확인
        when(notification.getReceiverId()).thenReturn(userId);

        // 실행
        notificationCommanderService.isReadCheck(id, userId);

        // then
        verify(notification).markAsRead();
        verify(notificationCommander).save(notification);
    }

    @Test
    @DisplayName("알림 읽기 실패 : 알림이 없음")
    void fail_isReadCheck_NOT_FOUND() {
        // given
        Long id = 1L;
        Long userId = 1L;

        // when
        // 알림 찾기
        when(notificationCommander.findByIdIsReadFalse(id)).thenReturn(Optional.empty());

        // 실행
        BaseException exception = assertThrows(BaseException.class, () ->
                notificationCommanderService.isReadCheck(id, userId)
        );

        // then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("알림을 찾을 수 없습니다!", exception.getMessage());
    }

    @Test
    @DisplayName("알림 읽기 실패 : 본인이 아님")
    void fail_isReadCheck_not_my() {
        // given
        Long id = 1L;
        Long userId = 1L;
        Notification notification = mock(Notification.class);

        // when
        // 알림 찾기
        when(notificationCommander.findByIdIsReadFalse(id)).thenReturn(Optional.of(notification));

        // 본인것인지 확인
        when(notification.getReceiverId()).thenReturn(2L);

        // 실행
        BaseException exception = assertThrows(BaseException.class, () ->
                notificationCommanderService.isReadCheck(id, userId)
        );

        // then
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("본인이 아닙니다!", exception.getMessage());
    }
}
