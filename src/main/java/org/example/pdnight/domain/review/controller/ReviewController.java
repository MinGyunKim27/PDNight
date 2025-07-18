package org.example.pdnight.domain.review.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.review.dto.request.ReviewRequestDto;
import org.example.pdnight.domain.review.dto.response.ReviewResponseDto;
import org.example.pdnight.domain.review.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/api/posts/{postId}/participants/{userId}/review")
    public ResponseEntity<ApiResponse<ReviewResponseDto>> createReview(
            @PathVariable Long postId,
            @PathVariable("userId") Long ratedUserId,
            @Validated @RequestBody ReviewRequestDto requestDto
    ) {
        // todo: 추후 유저 @AuthenticationPrincipal를 통해 받아오기
        Long userId = 1L;
        ReviewResponseDto response = reviewService.createReview(userId, ratedUserId, postId, requestDto);

        return ResponseEntity.ok(ApiResponse.ok("리뷰가 등록되었습니다.", response));
    }
}
