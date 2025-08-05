package org.example.pdnight.domain.notification.presentation;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.notification.application.NotificationConsumeService;
import org.example.pdnight.domain.notification.presentation.dto.NotificationResponse;
import org.example.pdnight.global.common.dto.ApiResponse;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationConsumeService notificationConsumeService;

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationResponse>> isRead(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        notificationConsumeService.isReadCheck(id, userId);
        return ResponseEntity.ok(ApiResponse.ok("읽음 확인", null));
    }

}
