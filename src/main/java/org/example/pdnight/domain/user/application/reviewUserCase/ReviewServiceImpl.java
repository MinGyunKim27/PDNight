package org.example.pdnight.domain.user.application.reviewUserCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.user.presentation.dto.reviewDto.request.ReviewRequestDto;
import org.example.pdnight.domain.user.presentation.dto.reviewDto.response.ReviewResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewQueryService reviewQueryService;
    private final ReviewCommandService reviewCommandService;

    @Override
    public ReviewResponseDto createReview(Long userId, Long ratedUserId, Long postId, ReviewRequestDto requestDto) {
        return reviewCommandService.createReview(userId, ratedUserId, postId, requestDto);
    }

    @Override
    public PagedResponse<ReviewResponseDto> getReceivedReviewsByUser(Long userId, Pageable pageable) {
        return reviewQueryService.getReceivedReviewsByUser(userId, pageable);
    }

    @Override
    public PagedResponse<ReviewResponseDto> getWrittenReviewsByUser(Long userId, Pageable pageable) {
        return reviewQueryService.getWrittenReviewsByUser(userId, pageable);
    }
}
