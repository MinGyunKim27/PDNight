package org.example.pdnight.domain.user.application.reviewUserCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.helper.GetHelper;
import org.example.pdnight.domain.user.domain.entity.Review;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.infra.reviewInfra.ReviewJpaRepository;
import org.example.pdnight.domain.user.presentation.dto.reviewDto.response.ReviewResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewQueryService {

    private final ReviewJpaRepository reviewJpaRepository;
    private final GetHelper helper;

    // 사용자의 받은 리뷰 리스트 조회
    public PagedResponse<ReviewResponseDto> getReceivedReviewsByUser(Long userId, Pageable pageable) {

        User ratedUser = helper.getUserByIdOrElseThrow(userId);

        Page<Review> reviews = reviewJpaRepository.findByRatedUser(ratedUser, pageable);

        return PagedResponse.from(reviews.map(ReviewResponseDto::from));
    }

    // 사용자의 작성한 리뷰 리스트 조회
    public PagedResponse<ReviewResponseDto> getWrittenReviewsByUser(Long userId, Pageable pageable) {

        User ratedUser = helper.getUserByIdOrElseThrow(userId);

        Page<Review> reviews = reviewJpaRepository.findByReviewer(ratedUser, pageable);

        return PagedResponse.from(reviews.map(ReviewResponseDto::from));
    }
}
