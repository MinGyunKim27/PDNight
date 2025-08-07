package org.example.pdnight.domain.notification.presentation;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.notification.application.notificationUseCase.NotificationService;
import org.example.pdnight.domain.notification.presentation.dto.NotificationResponse;
import org.example.pdnight.global.common.dto.ApiResponse;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationResponse>> isRead(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        notificationService.isReadCheck(id, userId);
        return ResponseEntity.ok(ApiResponse.ok("알림을 읽었습니다.", null));
    }

    // 사용자의 모든 알림 조회
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<PagedResponse<NotificationResponse>>> getNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = userDetails.getUserId();
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<NotificationResponse> response = notificationService.findAllNotification(userId, pageable);
        return ResponseEntity.ok(ApiResponse.ok("사용자의 모든 알림을 조회했습니다.", response));
    }

    // 사용자의 읽지 않은 알림 조회
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<PagedResponse<NotificationResponse>>> getIsReadFalseNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = userDetails.getUserId();
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<NotificationResponse> response = notificationService.findIsReadFalseNotification(userId, pageable);
        return ResponseEntity.ok(ApiResponse.ok("읽지 않은 알림을 조회했습니다.", response));
    }
}
