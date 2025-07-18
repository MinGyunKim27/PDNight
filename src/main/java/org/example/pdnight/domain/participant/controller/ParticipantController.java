package org.example.pdnight.domain.participant.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.participant.dto.response.ParticipantResponse;
import org.example.pdnight.domain.participant.service.ParticipantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/participant/posts")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;

    @PostMapping("/{postId}")
    public ResponseEntity<ApiResponse<ParticipantResponse>> signup(
            @PathVariable Long postId
    ) {
        Long userId = 1L; // user 추가 전 임시 사용
        ParticipantResponse response = participantService.applyParticipant(userId, postId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("참여 신청되었습니다.", response));
    }

}
