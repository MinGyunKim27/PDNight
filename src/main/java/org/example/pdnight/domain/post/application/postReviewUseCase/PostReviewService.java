package org.example.pdnight.domain.post.application.postReviewUseCase;

import jakarta.validation.Valid;
import org.example.pdnight.domain.post.presentation.dto.request.PostReviewRequest;
import org.example.pdnight.domain.post.presentation.dto.request.PostReviewUpdateRequest;
import org.example.pdnight.domain.post.presentation.dto.response.PostReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface PostReviewService {
    PostReviewResponse createPostReview(Long userId, @Valid PostReviewRequest request) throws IOException;

    Page<PostReviewResponse> searchPostReviewPage(Pageable pageable, Long postId);

    PostReviewResponse searchPostReview(String reviewId);

    PostReviewResponse updatePostReview(Long userId, String reviewId, PostReviewUpdateRequest request) throws IOException;

    void deletePostReview(Long userId, String reviewId);
}
