package org.example.pdnight.domain.user.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.user.presentation.dto.reviewDto.request.ReviewRequest;
import org.example.pdnight.domain.user.presentation.dto.reviewDto.response.ReviewResponse;
import org.example.pdnight.domain.user.application.reviewUserCase.ReviewServiceImpl;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewServiceImpl reviewServiceImpl;

    // -------------------------- Command Api -------------------------------------------------//
    @PostMapping("/posts/{postId}/participants/{userId}/review")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @PathVariable("userId") Long ratedUserId,
            @Validated @RequestBody ReviewRequest requestDto
    ) {
        ReviewResponse response = reviewServiceImpl.createReview(userDetails.getUserId(), ratedUserId, postId, requestDto);

        return ResponseEntity.ok(ApiResponse.ok("리뷰가 등록되었습니다.", response));
    }

    // ---------------------- 조회 Api ---------------------------------------------------------//

    //사용자가 받은 리뷰 리스트 조회
    @GetMapping("/users/{userId}/review")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewResponse>>> getReviews(
            @PathVariable("userId") Long userId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok("사용자가 받은 리뷰 리스트 조회 성공.", reviewServiceImpl.getReceivedReviewsByUser(userId, pageable)));
    }

    //내가 받은 리뷰 리스트 조회
    @GetMapping("/users/my/review")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewResponse>>> getMyReviews(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long myId = userDetails.getUserId();
        return ResponseEntity.ok(ApiResponse.ok("사용자가 받은 리뷰 리스트 조회 성공.", reviewServiceImpl.getReceivedReviewsByUser(myId, pageable)));
    }

    //내가 받은 리뷰 리스트 조회
    @GetMapping("/users/my/writtenReview")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewResponse>>> getMyWrittenReviews(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long myId = userDetails.getUserId();
        return ResponseEntity.ok(ApiResponse.ok("사용자가 받은 리뷰 리스트 조회 성공.", reviewServiceImpl.getWrittenReviewsByUser(myId, pageable)));
    }
}
