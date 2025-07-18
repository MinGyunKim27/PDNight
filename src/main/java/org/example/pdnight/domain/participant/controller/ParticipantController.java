package org.example.pdnight.domain.participant.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.participant.dto.response.ParticipantResponse;
import org.example.pdnight.domain.participant.service.ParticipantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/participant/posts")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;

    @PostMapping("/{postId}")
    public ResponseEntity<ApiResponse<ParticipantResponse>> applyParticipant(
            @RequestParam Long userId,
            @PathVariable Long postId
    ) {
        ParticipantResponse response = participantService.applyParticipant(userId, postId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("참여 신청되었습니다.", response));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deleteParticipant(
            @RequestParam Long userId,
            @PathVariable Long postId
    ) {
        participantService.deleteParticipant(userId, postId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("참여 신청이 취소되었습니다.", null));
    }

    @PatchMapping("/{postId}/users/{userId}")
    public ResponseEntity<ApiResponse<ParticipantResponse>> changeStatusParticipant(
            @PathVariable Long postId,
            @PathVariable Long userId,
            @RequestParam String status
    ) {
        ParticipantResponse response = participantService.changeStatusParticipant(userId, postId, status);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("신청자가 수락 혹은 거절되었습니다.", response));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PagedResponse<ParticipantResponse>>> getPendingParticipantList(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PagedResponse<ParticipantResponse> response = participantService.getPendingParticipantList(postId, page, size);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("신청자 목록이 조회되었습니다.", response));
    }

}
