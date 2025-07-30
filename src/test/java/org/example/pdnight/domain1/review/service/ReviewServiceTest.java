package org.example.pdnight.domain1.review.service;

import org.example.pdnight.domain1.common.dto.PagedResponse;
import org.example.pdnight.domain1.common.enums.ErrorCode;
import org.example.pdnight.domain1.common.exception.BaseException;
import org.example.pdnight.domain1.common.helper.GetHelper;
import org.example.pdnight.domain1.post.entity.Post;
import org.example.pdnight.domain1.review.dto.request.ReviewRequestDto;
import org.example.pdnight.domain1.review.dto.response.ReviewResponseDto;
import org.example.pdnight.domain1.review.entity.Review;
import org.example.pdnight.domain1.review.repository.ReviewRepository;
import org.example.pdnight.domain1.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private GetHelper helper;


    @Test
    @DisplayName("리뷰 등록 테스트 성공")
    void 리뷰_등록_테스트_성공() {
        // given
        Long reviewerId = 1L;
        Long ratedUserId = 2L;
        Long postId = 3L;

        // User reviewer = new User(reviewerId, "리뷰어", 0L, 0L);
        // User ratedUser = new User(ratedUserId, "피리뷰어", 0L, 0L);
        User reviewer = User.createTestUser(reviewerId,"리뷰어", "a@a.com","");
        User ratedUser = User.createTestUser(ratedUserId, "피리뷰어", "b@b.com", "");
        reviewer.setId(reviewerId);
        ratedUser.setId(ratedUserId);
        Post post = Mockito.mock();  // 테스트 전용 생성자 필요할 수 있음
        when(post.getId()).thenReturn(3L);     // 또는 Post 클래스에도 테스트 생성자 추가 권장

        ReviewRequestDto requestDto = mock(ReviewRequestDto.class);
        when(requestDto.getRate()).thenReturn(BigDecimal.ONE);

        when(helper.getUserByIdOrElseThrow(reviewerId)).thenReturn(reviewer);
        when(helper.getUserByIdOrElseThrow(ratedUserId)).thenReturn(ratedUser);
        when(helper.getPostByIdOrElseThrow(postId)).thenReturn(post);
        when(reviewRepository.existsByReviewerAndRatedUserAndPost(reviewer, ratedUser, post)).thenReturn(false);

        Review savedReview = Review.create(reviewer, ratedUser, post, requestDto.getRate(), requestDto.getComment());
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        // when
        ReviewResponseDto response = reviewService.createReview(reviewerId, ratedUserId, postId, requestDto);

        // then
        assertNotNull(response);
        assertEquals(BigDecimal.ONE, response.getRate());
        assertEquals(ratedUserId, response.getRatedUserId());
        assertEquals(postId, response.getPostId());
    }

    @Test
    @DisplayName("자기 자신을 리뷰하려 할 때 예외 발생")
    void 자기자신_리뷰_예외() {
        // given
        Long userId = 1L;
        Long postId = 1L;
        ReviewRequestDto mockRequestDto = mock(ReviewRequestDto.class);

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                reviewService.createReview(userId, userId, postId, mockRequestDto)
        );
        assertEquals(ErrorCode.CANNOT_REVIEW_SELF.getStatus(), exception.getStatus());
        assertEquals(ErrorCode.CANNOT_REVIEW_SELF.getMessage(), exception.getMessage());
    }


    @Test
    @DisplayName("이미 리뷰한 경우 예외 발생")
    void 중복리뷰_예외() {
        // given
        User reviewer = mock();
        User ratedUser = mock();
        Post post = mock();

        ReviewRequestDto requestDto = mock(ReviewRequestDto.class);

        when(helper.getUserByIdOrElseThrow(1L)).thenReturn(reviewer);
        when(helper.getUserByIdOrElseThrow(2L)).thenReturn(ratedUser);
        when(helper.getPostByIdOrElseThrow(any())).thenReturn(post);
        when(reviewRepository.existsByReviewerAndRatedUserAndPost(reviewer, ratedUser, post)).thenReturn(true);

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                reviewService.createReview(1L, 2L, 1L, requestDto)
        );
        assertEquals(ErrorCode.ALREADY_REVIEWED.getStatus(), exception.getStatus());
        assertEquals(ErrorCode.ALREADY_REVIEWED.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("받은 리뷰 리스트 조회 테스트")
    void 받은_리뷰_리스트_조회() {
        // given
        Long userId = 1L;

        User ratedUser = User.createTestUser(userId, "받은사람", "b@b.com", "");
        User reviewer = User.createTestUser(2L,"작성자", "a@a.com","");

        Post post = Mockito.mock(Post.class);
        // 필수 정보 초기화
        ReflectionTestUtils.setField(post, "id", 100L); // 또는 mock(post).when(getId()).thenReturn(100L);

        Review review = Review.create(reviewer, ratedUser, post, BigDecimal.ONE, null); // 실제 객체 생성


        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Review> reviewPage = new PageImpl<>(List.of(review), pageable, 1);

        when(helper.getUserByIdOrElseThrow(userId)).thenReturn(ratedUser);
        when(reviewRepository.findByRatedUser(ratedUser, pageable)).thenReturn(reviewPage);

        // when
        PagedResponse<ReviewResponseDto> response = reviewService.getReceivedReviewsByUser(userId, pageable);

        // then
        assertNotNull(response);
        assertEquals(1, response.contents().size());
        assertEquals(BigDecimal.ONE, response.contents().get(0).getRate());
    }


    @Test
    @DisplayName("작성한 리뷰 리스트 조회 테스트")
    void 작성한_리뷰_리스트_조회() {
        // given
        Long userId = 2L;
        Long reviewerId = 3L;
        // User reviewer = new User(userId, "작성자", 0L, 0L);
        // User ratedUser = new User(3L, "피리뷰어", 0L, 0L);
        User ratedUser = mock();
        User reviewer = mock();

        when(ratedUser.getId()).thenReturn(reviewerId);

        Post post = Mockito.mock(Post.class);
        ReflectionTestUtils.setField(post, "id", 0L);

        // Review review = new Review();
        Review review = mock();
        when(review.getPost()).thenReturn(post);
        when(review.getRatedUser()).thenReturn(ratedUser);
        when(review.getRate()).thenReturn(BigDecimal.ONE);


        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Review> reviewPage = new PageImpl<>(List.of(review), pageable, 1);

        when(helper.getUserByIdOrElseThrow(userId)).thenReturn(reviewer);
        when(reviewRepository.findByReviewer(reviewer, pageable)).thenReturn(reviewPage);

        // when
        PagedResponse<ReviewResponseDto> response = reviewService.getWrittenReviewsByUser(userId, pageable);

        // then
        assertNotNull(response);
        assertEquals(1, response.contents().size());
        assertEquals(BigDecimal.ONE, response.contents().get(0).getRate());
        assertEquals(0L, response.contents().get(0).getPostId());
        assertEquals(3L, response.contents().get(0).getRatedUserId());
    }

}
