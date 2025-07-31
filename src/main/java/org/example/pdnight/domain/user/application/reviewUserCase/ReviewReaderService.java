package org.example.pdnight.domain.user.application.reviewUserCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.user.domain.entity.Review;
import org.example.pdnight.domain.user.domain.reviewDomain.ReviewReader;
import org.example.pdnight.domain.user.presentation.dto.reviewDto.response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewReaderService {

    private final ReviewReader reviewReader;

    // 사용자의 받은 리뷰 리스트 조회
    @Transactional(readOnly = true)
    public PagedResponse<ReviewResponse> getReceivedReviewsByUser(Long userId, Pageable pageable) {
        Page<Review> reviews = reviewReader.findByReviewee(userId, pageable);

        return PagedResponse.from(reviews.map(ReviewResponse::from));
    }

    // 사용자의 작성한 리뷰 리스트 조회
    @Transactional(readOnly = true)
    public PagedResponse<ReviewResponse> getWrittenReviewsByUser(Long userId, Pageable pageable) {
        Page<Review> reviews = reviewReader.findByReviewer(userId, pageable);

        return PagedResponse.from(reviews.map(ReviewResponse::from));
    }
}
