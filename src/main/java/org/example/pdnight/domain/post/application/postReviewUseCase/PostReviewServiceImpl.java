package org.example.pdnight.domain.post.application.postReviewUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.presentation.dto.request.PostReviewRequest;
import org.example.pdnight.domain.post.presentation.dto.request.PostReviewUpdateRequest;
import org.example.pdnight.domain.post.presentation.dto.response.PostReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PostReviewServiceImpl implements PostReviewService {
    private final PostReviewCommanderService postReviewCommanderService;
    private final PostReviewReaderService postReviewReaderService;

    @Override
    public PostReviewResponse createPostReview(Long userId, PostReviewRequest request) throws IOException {
        return postReviewCommanderService.createPostReview(userId, request);
    }

    @Override
    public Page<PostReviewResponse> searchPostReviewPage(Pageable pageable, Long postId) {
        return postReviewReaderService.searchPostReviewPage(pageable, postId);
    }

    @Override
    public PostReviewResponse searchPostReview(String reviewId) {
        return postReviewReaderService.searchPostReview(reviewId);
    }

    @Override
    public PostReviewResponse updatePostReview(Long userId, String reviewId, PostReviewUpdateRequest request) throws IOException {
        return postReviewCommanderService.updatePostReview(userId, reviewId, request);
    }

    @Override
    public void deletePostReview(Long userId, String reviewId) {
        postReviewCommanderService.deletePostReview(userId, reviewId);
    }
}
