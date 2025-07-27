package org.example.pdnight.domain.participant.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.participant.dto.response.ParticipantResponse;
import org.example.pdnight.domain.participant.service.ParticipantService;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;

    @PostMapping("/api/posts/{postId}/participate")
    public ResponseEntity<ApiResponse<ParticipantResponse>> applyParticipant(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @PathVariable Long postId
    ) {
        ParticipantResponse response = participantService.applyParticipant(loginUser.getUserId(), postId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("참여 신청되었습니다.", response));
    }

    @DeleteMapping("/api/posts/{postId}/participate")
    public ResponseEntity<ApiResponse<Void>> deleteParticipant(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @PathVariable Long postId
    ) {
        participantService.deleteParticipant(loginUser.getUserId(), postId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("참여 신청이 취소되었습니다.", null));
    }

    @PatchMapping("/api/posts/{postId}/participate/users/{userId}")
    public ResponseEntity<ApiResponse<ParticipantResponse>> changeStatusParticipant(
            @AuthenticationPrincipal CustomUserDetails author,
            @PathVariable Long postId,
            @PathVariable Long userId,
            @RequestParam String status
    ) {
        ParticipantResponse response = participantService.changeStatusParticipant(author.getUserId(), userId, postId, status);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("신청자가 수락 혹은 거절되었습니다.", response));
    }

    @GetMapping("/api/posts/{postId}/participant")
    public ResponseEntity<ApiResponse<PagedResponse<ParticipantResponse>>> getPendingParticipantList(
            @AuthenticationPrincipal CustomUserDetails author,
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PagedResponse<ParticipantResponse> response = participantService.getParticipantListByPending(author.getUserId(), postId, page, size);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("신청자 목록이 조회되었습니다.", response));
    }

    @GetMapping("/api/posts/{postId}/participate/confirmed")
    public ResponseEntity<ApiResponse<PagedResponse<ParticipantResponse>>> getAcceptedParticipantList(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PagedResponse<ParticipantResponse> response = participantService.getParticipantListByAccepted(loginUser.getUserId(), postId, page, size);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("참여자 목록이 조회되었습니다.", response));
    }

}
