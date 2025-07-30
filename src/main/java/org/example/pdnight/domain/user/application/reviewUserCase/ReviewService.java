package org.example.pdnight.domain.user.application.reviewUserCase;

import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.user.presentation.dto.reviewDto.request.ReviewRequestDto;
import org.example.pdnight.domain.user.presentation.dto.reviewDto.response.ReviewResponseDto;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    ReviewResponseDto createReview(Long userId, Long ratedUserId, Long postId, ReviewRequestDto requestDto);

    PagedResponse<ReviewResponseDto> getReceivedReviewsByUser(Long userId, Pageable pageable);

    PagedResponse<ReviewResponseDto> getWrittenReviewsByUser(Long userId, Pageable pageable);
}
