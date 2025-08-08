package org.example.pdnight.domain.user.application.reviewUseCase;

import org.example.pdnight.domain.user.application.reviewUserCase.ReviewCommanderService;
import org.example.pdnight.domain.user.domain.entity.Review;
import org.example.pdnight.domain.user.domain.reviewDomain.ReviewCommander;
import org.example.pdnight.domain.user.domain.reviewDomain.ReviewProducer;
import org.example.pdnight.domain.user.presentation.dto.reviewDto.request.ReviewRequest;
import org.example.pdnight.domain.user.presentation.dto.reviewDto.response.ReviewResponse;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewCommanderServiceTest {

    @InjectMocks
    private ReviewCommanderService reviewService;

    @Mock
    private ReviewCommander reviewCommander;

    @Mock
    private ReviewProducer reviewProducer;

    @Test
    @DisplayName("리뷰 등록 테스트 성공")
    void createReview() {
        // given
        Long userId = 1L;
        Long ratedUserId = 2L;
        Long postId = 3L;

        BigDecimal rate = BigDecimal.ONE;
        String comment = "평가 내용";
        ReviewRequest request = mock(ReviewRequest.class);
        when(request.getRate()).thenReturn(rate);
        when(request.getComment()).thenReturn(comment);
        doNothing().when(reviewProducer).produce(anyString(), any());

        // 존재 여부 판단
        when(reviewCommander.isExistsByUsersAndPost(userId, ratedUserId, postId)).thenReturn(false);

        Review review = Review.create(userId, ratedUserId, postId, request.getRate(), request.getComment());
        when(reviewCommander.save(any(Review.class))).thenReturn(review);

        // when
        ReviewResponse response = reviewService.createReview(userId, ratedUserId, postId, request);

        // then
        assertNotNull(response);
        assertEquals(userId, response.getReviewerId());
        assertEquals(ratedUserId, response.getRevieweeId());
        assertEquals(postId, response.getPostId());
        assertEquals(rate, response.getRate());
        assertEquals(comment, response.getComment());
    }

    @Test
    @DisplayName("자기 자신을 리뷰하려 할 때 예외 발생")
    void fail_createReview_selfReview() {
        // given
        Long userId = 1L;
        Long postId = 3L;
        ReviewRequest request = mock(ReviewRequest.class);

        // when
        BaseException exception = assertThrows(BaseException.class, () ->
                reviewService.createReview(userId, userId, postId, request)
        );

        // then
        assertEquals(ErrorCode.CANNOT_REVIEW_SELF.getStatus(), exception.getStatus());
        assertEquals(ErrorCode.CANNOT_REVIEW_SELF.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("이미 리뷰한 경우 예외 발생")
    void fail_createReview_validateExists() {
        // given
        Long userId = 1L;
        Long ratedUserId = 2L;
        Long postId = 3L;
        ReviewRequest request = mock(ReviewRequest.class);

        // 존재 여부 판단
        when(reviewCommander.isExistsByUsersAndPost(userId, ratedUserId, postId)).thenReturn(true);

        // when
        BaseException exception = assertThrows(BaseException.class, () ->
                reviewService.createReview(userId, ratedUserId, postId, request)
        );

        // then
        assertEquals(ErrorCode.ALREADY_REVIEWED.getStatus(), exception.getStatus());
        assertEquals(ErrorCode.ALREADY_REVIEWED.getMessage(), exception.getMessage());
    }
}
