package org.example.pdnight.domain.post.application.postReviewUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.postReview.PostReviewDocument;
import org.example.pdnight.domain.post.domain.postReview.PostReviewReader;
import org.example.pdnight.domain.post.presentation.dto.response.PostReviewResponse;
import org.example.pdnight.global.common.exception.BaseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import static org.example.pdnight.global.common.enums.ErrorCode.POST_REVIEW_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class PostReviewReaderService {
    private final PostReviewReader postReviewReader;

    public Page<PostReviewResponse> searchPostReviewPage(Pageable pageable, Long postId) {
        Page<PostReviewDocument> postReviewPage = postReviewReader.ReviewPage(pageable, postId);
        return postReviewPage.map(PostReviewResponse::create);
    }

    public PostReviewResponse searchPostReview(String reviewId) {
        PostReviewDocument postReview = postReviewReader.findById(reviewId).orElseThrow(() -> new BaseException(POST_REVIEW_NOT_FOUND));
        return PostReviewResponse.create(postReview);
    }
}
