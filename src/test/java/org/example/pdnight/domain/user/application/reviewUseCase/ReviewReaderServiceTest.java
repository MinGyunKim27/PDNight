package org.example.pdnight.domain.user.application.reviewUseCase;

import org.example.pdnight.domain.user.application.reviewUserCase.ReviewReaderService;
import org.example.pdnight.domain.user.domain.entity.Review;
import org.example.pdnight.domain.user.domain.reviewDomain.ReviewReader;
import org.example.pdnight.domain.user.presentation.dto.reviewDto.request.ReviewRequest;
import org.example.pdnight.domain.user.presentation.dto.reviewDto.response.ReviewResponse;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewReaderServiceTest {

    @InjectMocks
    private ReviewReaderService reviewService;

    @Mock
    private ReviewReader reviewReader;


    @Test
    @DisplayName("받은 리뷰 리스트 조회 테스트")
    void getReceivedReviewsByUser() {
        // given
        Long userId = 1L;
        Long ratedUserId = 2L;
        Long postId = 3L;

        BigDecimal rate = BigDecimal.ONE;
        String comment = "평가 내용";
        ReviewRequest request = mock(ReviewRequest.class);
        when(request.getRate()).thenReturn(rate);
        when(request.getComment()).thenReturn(comment);

        // 결과물 생성
        Review review = Review.create(userId, ratedUserId, postId, request.getRate(), request.getComment());

        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Review> reviewPage = new PageImpl<>(List.of(review), pageable, 1);

        when(reviewReader.findByReviewee(userId, pageable)).thenReturn(reviewPage);

        // when
        PagedResponse<ReviewResponse> response = reviewService.getReceivedReviewsByUser(userId, pageable);

        // then
        assertNotNull(response);
        assertEquals(1, response.contents().size());
        assertEquals(userId, response.contents().get(0).getReviewerId());
        assertEquals(ratedUserId, response.contents().get(0).getRevieweeId());
        assertEquals(postId, response.contents().get(0).getPostId());
        assertEquals(rate, response.contents().get(0).getRate());
        assertEquals(comment, response.contents().get(0).getComment());
    }

    @Test
    @DisplayName("작성한 리뷰 리스트 조회 테스트")
    void getWrittenReviewsByUser() {
        // given
        Long userId = 1L;
        Long ratedUserId = 2L;
        Long postId = 3L;

        BigDecimal rate = BigDecimal.ONE;
        String comment = "평가 내용";
        ReviewRequest request = mock(ReviewRequest.class);
        when(request.getRate()).thenReturn(rate);
        when(request.getComment()).thenReturn(comment);

        // 결과물 생성
        Review review = Review.create(userId, ratedUserId, postId, request.getRate(), request.getComment());

        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Review> reviewPage = new PageImpl<>(List.of(review), pageable, 1);

        when(reviewReader.findByReviewer(ratedUserId, pageable)).thenReturn(reviewPage);

        // when
        PagedResponse<ReviewResponse> response = reviewService.getWrittenReviewsByUser(ratedUserId, pageable);

        // then
        assertNotNull(response);
        assertEquals(1, response.contents().size());
        assertEquals(userId, response.contents().get(0).getReviewerId());
        assertEquals(ratedUserId, response.contents().get(0).getRevieweeId());
        assertEquals(postId, response.contents().get(0).getPostId());
        assertEquals(rate, response.contents().get(0).getRate());
        assertEquals(comment, response.contents().get(0).getComment());
    }
}
