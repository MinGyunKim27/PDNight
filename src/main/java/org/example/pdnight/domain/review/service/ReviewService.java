package org.example.pdnight.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.repository.PostRepository;
import org.example.pdnight.domain.review.dto.request.ReviewRequestDto;
import org.example.pdnight.domain.review.dto.response.ReviewResponseDto;
import org.example.pdnight.domain.review.entity.Review;
import org.example.pdnight.domain.review.repository.ReviewRepository;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;


    @Transactional
    public ReviewResponseDto createReview(Long userId, Long ratedUserId, Long postId, ReviewRequestDto requestDto) {
        if (userId.equals(ratedUserId)) {
            throw new BaseException(ErrorCode.CANNOT_REVIEW_SELF);
        }

        User reviewer = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
        User ratedUser = userRepository.findById(ratedUserId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));

        if (reviewRepository.existsByReviewerAndRatedUserAndPost(reviewer, ratedUser, post)) {
            throw new BaseException(ErrorCode.ALREADY_REVIEWED);
        }

        Review review = new Review(reviewer, ratedUser, post, requestDto);

        reviewRepository.save(review);
        post.addReview(review);

        return new ReviewResponseDto(review);
    }

    // 사용자의 받은 리뷰 리스트 조회
    public PagedResponse<ReviewResponseDto> getReceivedReviewsByUser(Long userId, Pageable pageable) {

        User ratedUser = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        Page<Review> reviews = reviewRepository.findByRatedUser(ratedUser, pageable);

        return PagedResponse.from(reviews.map(ReviewResponseDto::new));
    }

    // 사용자의 작성한 리뷰 리스트 조회
    public PagedResponse<ReviewResponseDto> getWrittenReviewsByUser(Long userId, Pageable pageable) {
        User ratedUser = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        Page<Review> reviews = reviewRepository.findByReviewer(ratedUser, pageable);

        return PagedResponse.from(reviews.map(ReviewResponseDto::new));
    }
}
