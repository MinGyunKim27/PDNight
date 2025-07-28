package org.example.pdnight.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.common.helper.GetHelper;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.review.dto.request.ReviewRequestDto;
import org.example.pdnight.domain.review.dto.response.ReviewResponseDto;
import org.example.pdnight.domain.review.entity.Review;
import org.example.pdnight.domain.review.repository.ReviewRepository;
import org.example.pdnight.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final GetHelper helper;

    @Transactional
    public ReviewResponseDto createReview(Long userId, Long ratedUserId, Long postId, ReviewRequestDto requestDto) {

        //본인이 본인을 매길 수 없음
        verifyAuthorize(userId, ratedUserId);

        User reviewer = helper.getUserById(userId);
        User ratedUser = helper.getUserById(ratedUserId);
        Post post = helper.getPostById(postId);

        // 존재 여부 판단
        validateExists(reviewer, ratedUser, post);

        Review review = Review.create(reviewer, ratedUser, post, requestDto);

        reviewRepository.save(review);
        post.addReview(review);

        return ReviewResponseDto.from(review);
    }

    // 사용자의 받은 리뷰 리스트 조회
    public PagedResponse<ReviewResponseDto> getReceivedReviewsByUser(Long userId, Pageable pageable) {

        User ratedUser = helper.getUserById(userId);

        Page<Review> reviews = reviewRepository.findByRatedUser(ratedUser, pageable);

        return PagedResponse.from(reviews.map(ReviewResponseDto::from));
    }

    // 사용자의 작성한 리뷰 리스트 조회
    public PagedResponse<ReviewResponseDto> getWrittenReviewsByUser(Long userId, Pageable pageable) {

        User ratedUser = helper.getUserById(userId);

        Page<Review> reviews = reviewRepository.findByReviewer(ratedUser, pageable);

        return PagedResponse.from(reviews.map(ReviewResponseDto::from));
    }

    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // get

    // validate
    private void validateExists(User reviewer, User ratedUser, Post post) {
        if (reviewRepository.existsByReviewerAndRatedUserAndPost(reviewer, ratedUser, post)) {
            throw new BaseException(ErrorCode.ALREADY_REVIEWED);
        }
    }

    private void verifyAuthorize(Long userId, Long ratedUserId) {
        if (userId.equals(ratedUserId)) {
            throw new BaseException(ErrorCode.CANNOT_REVIEW_SELF);
        }
    }

}
