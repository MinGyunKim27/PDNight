package org.example.pdnight.domain.user.application.reviewUserCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.common.helper.GetHelper;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.user.domain.entity.Review;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.infra.reviewInfra.ReviewJpaRepository;
import org.example.pdnight.domain.user.presentation.dto.reviewDto.request.ReviewRequestDto;
import org.example.pdnight.domain.user.presentation.dto.reviewDto.response.ReviewResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewCommandService {

    private final ReviewJpaRepository reviewJpaRepository;
    private final GetHelper helper;

    @Transactional
    public ReviewResponseDto createReview(Long userId, Long ratedUserId, Long postId, ReviewRequestDto requestDto) {

        //본인이 본인을 매길 수 없음
        verifyAuthorize(userId, ratedUserId);

        User reviewer = helper.getUserByIdOrElseThrow(userId);
        User ratedUser = helper.getUserByIdOrElseThrow(ratedUserId);
        Post post = helper.getPostByIdOrElseThrow(postId);

        // 존재 여부 판단
        validateExists(reviewer, ratedUser, post);

        Review review = Review.create(reviewer, ratedUser, post, requestDto.getRate(), requestDto.getComment());

        reviewJpaRepository.save(review);
        post.addReview(review);

        return ReviewResponseDto.from(review);
    }

    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // validate
    private void validateExists(User reviewer, User ratedUser, Post post) {
        if (reviewJpaRepository.existsByReviewerAndRatedUserAndPost(reviewer, ratedUser, post)) {
            throw new BaseException(ErrorCode.ALREADY_REVIEWED);
        }
    }

    private void verifyAuthorize(Long userId, Long ratedUserId) {
        if (userId.equals(ratedUserId)) {
            throw new BaseException(ErrorCode.CANNOT_REVIEW_SELF);
        }
    }

}
