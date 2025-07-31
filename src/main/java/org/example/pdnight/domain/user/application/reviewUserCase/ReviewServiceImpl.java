package org.example.pdnight.domain.user.application.reviewUserCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.user.presentation.dto.reviewDto.request.ReviewRequest;
import org.example.pdnight.domain.user.presentation.dto.reviewDto.response.ReviewResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewReaderService reviewReaderService;
    private final ReviewCommanderService reviewCommanderService;

    @Override
    public ReviewResponse createReview(Long userId, Long ratedUserId, Long postId, ReviewRequest requestDto) {
        return reviewCommanderService.createReview(userId, ratedUserId, postId, requestDto);
    }

    @Override
    public PagedResponse<ReviewResponse> getReceivedReviewsByUser(Long userId, Pageable pageable) {
        return reviewReaderService.getReceivedReviewsByUser(userId, pageable);
    }

    @Override
    public PagedResponse<ReviewResponse> getWrittenReviewsByUser(Long userId, Pageable pageable) {
        return reviewReaderService.getWrittenReviewsByUser(userId, pageable);
    }
}
