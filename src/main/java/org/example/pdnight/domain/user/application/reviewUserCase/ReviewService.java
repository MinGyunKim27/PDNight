package org.example.pdnight.domain.user.application.reviewUserCase;

import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.user.presentation.dto.reviewDto.request.ReviewRequest;
import org.example.pdnight.domain.user.presentation.dto.reviewDto.response.ReviewResponse;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    ReviewResponse createReview(Long userId, Long ratedUserId, Long postId, ReviewRequest requestDto);

    PagedResponse<ReviewResponse> getReceivedReviewsByUser(Long userId, Pageable pageable);

    PagedResponse<ReviewResponse> getWrittenReviewsByUser(Long userId, Pageable pageable);
}
