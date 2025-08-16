package org.example.pdnight.domain.post.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.application.postReviewUseCase.PostReviewService;
import org.example.pdnight.domain.post.presentation.dto.request.PostReviewRequest;
import org.example.pdnight.domain.post.presentation.dto.request.PostReviewUpdateRequest;
import org.example.pdnight.domain.post.presentation.dto.response.PostReviewResponse;
import org.example.pdnight.global.common.dto.ApiResponse;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostReviewController {
    private final PostReviewService postReviewService;

    @PostMapping(value = "/post/review", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PostReviewResponse>> savePostReview(
            @Valid @ModelAttribute PostReviewRequest request,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) throws IOException {
        Long userId = loginUser.getUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("정상적으로 등록되었습니다.", postReviewService.createPostReview(userId, request)));
    }

    @GetMapping("/post/reviews")
    public ResponseEntity<ApiResponse<PagedResponse<PostReviewResponse>>> searchPostReviewPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long postId
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("리뷰 페이지가 조회되었습니다.", PagedResponse.from(postReviewService.searchPostReviewPage(pageable, postId))));
    }

    @GetMapping("/post/review/{reviewId}")
    public ResponseEntity<ApiResponse<PostReviewResponse>> searchPostReview(
            @PathVariable String reviewId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("리뷰가 조회되었습니다.", postReviewService.searchPostReview(reviewId)));
    }

    @PutMapping(value = "/post/review/{reviewId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PostReviewResponse>> updatePostReview(
            @PathVariable String reviewId,
            @ModelAttribute PostReviewUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) throws IOException {
        Long userId = loginUser.getUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("정상적으로 수정되었습니다.", postReviewService.updatePostReview(userId, reviewId, request)));
    }

    @DeleteMapping("/post/review/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deletePostReview(
            @PathVariable String reviewId,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long userId = loginUser.getUserId();
        postReviewService.deletePostReview(userId, reviewId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("정상적으로 삭제되었습니다.", null));
    }
}
