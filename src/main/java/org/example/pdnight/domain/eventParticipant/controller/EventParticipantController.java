package org.example.pdnight.domain.eventParticipant.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.eventParticipant.service.EventParticipantService;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events/{id}/participants")
@RequiredArgsConstructor
public class EventParticipantController {

    private final EventParticipantService eventParticipantService;

    // 이벤트 참가 신청
    @PostMapping
    public ResponseEntity<ApiResponse<?>> addParticipant(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ){
        Long userId = loginUser.getUserId();
        eventParticipantService.addParticipant(id, userId);
        return ResponseEntity.ok(
                ApiResponse.ok("이벤트 참가 신청 성공했습니다.", null)
        );
    }
}
