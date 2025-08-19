package org.example.pdnight.domain.notification.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Notification")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/{id}")
    @Operation(summary = "알림 읽음 확인", description = "받은 알림을 읽는다")
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
    @Operation(summary = "모든 알림 조회", description = "사용자가 받은 알림 목록을 조회한다")
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
    @Operation(summary = "읽지 않은 알림 조회", description = "사용자가 받은 알림 중 읽지 않은 알림 목록을 조회한다")
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
