package org.example.pdnight.domain.review.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.review.dto.request.ReviewRequestDto;
import org.example.pdnight.domain.review.dto.response.ReviewResponseDto;
import org.example.pdnight.domain.review.service.ReviewService;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/api/posts/{postId}/participants/{userId}/review")
    public ResponseEntity<ApiResponse<ReviewResponseDto>> createReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @PathVariable("userId") Long ratedUserId,
            @Validated @RequestBody ReviewRequestDto requestDto
    ) {
        ReviewResponseDto response = reviewService.createReview(userDetails.getUserId(), ratedUserId, postId, requestDto);

        return ResponseEntity.ok(ApiResponse.ok("리뷰가 등록되었습니다.", response));
    }

    //사용자가 받은 리뷰 리스트 조회
    @GetMapping("api/users/{userId}/review")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewResponseDto>>> getReviews(
            @PathVariable("userId") Long userId,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {

        return ResponseEntity.ok(ApiResponse.ok("사용자가 받은 리뷰 리스트 조회 성공.", reviewService.getReceivedReviewsByUser(userId, pageable)));
    }

    //내가 받은 리뷰 리스트 조회
    @GetMapping("api/users/my/review")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewResponseDto>>> getMyReviews(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long myId = userDetails.getUserId();
        return ResponseEntity.ok(ApiResponse.ok("사용자가 받은 리뷰 리스트 조회 성공.", reviewService.getReceivedReviewsByUser(myId, pageable)));
    }

    //내가 받은 리뷰 리스트 조회
    @GetMapping("api/users/my/writtenReview")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewResponseDto>>> getMyWrittenReviews(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long myId = userDetails.getUserId();
        return ResponseEntity.ok(ApiResponse.ok("사용자가 받은 리뷰 리스트 조회 성공.", reviewService.getWrittenReviewsByUser(myId, pageable)));
    }
}
