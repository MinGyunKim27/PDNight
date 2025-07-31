package org.example.pdnight.domain.user.application.reviewUserCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.user.application.userUseCase.event.UserEvaluationEvent;
import org.example.pdnight.domain.user.domain.entity.Review;
import org.example.pdnight.domain.user.domain.reviewDomain.ReviewCommander;
import org.example.pdnight.domain.user.domain.reviewDomain.ReviewReader;
import org.example.pdnight.domain.user.presentation.dto.reviewDto.request.ReviewRequest;
import org.example.pdnight.domain.user.presentation.dto.reviewDto.response.ReviewResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewCommanderService {

    private final ReviewReader reviewReader;
    private final ReviewCommander reviewCommander;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ReviewResponse createReview(Long userId, Long ratedUserId, Long postId, ReviewRequest requestDto) {
        // 존재 여부 판단
        validateExists(userId, ratedUserId, postId);

        Review review = Review.create(userId, ratedUserId, postId, requestDto.getRate(), requestDto.getComment());
        Review saveReview = reviewCommander.save(review);

        // user 평가 갱신 이벤트 발행
        eventPublisher.publishEvent(UserEvaluationEvent.of(
                userId,
                requestDto.getRate()
        ));

        return ReviewResponse.from(saveReview);
    }

    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    private void validateExists(Long reviewerId, Long revieweeId, Long postId) {
        if (reviewReader.isExistsByUsersAndPost(reviewerId, revieweeId, postId)) {
            throw new BaseException(ErrorCode.ALREADY_REVIEWED);
        }
    }
}
