package org.example.pdnight.domain.review.service;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;



    @Test
    @DisplayName("리뷰 등록 테스트 성공")
    void 리뷰_등록_테스트_성공() {
        //given
        Long userId1 = 1L;
        Long userId2 = 2L;
        Long postId = 1L;

        User mockUser1 = Mockito.mock(User.class);
        User mockUser2 = Mockito.mock(User.class);
        Post mockPost = Mockito.mock(Post.class);

        ReviewRequestDto mockRequestDto  = Mockito.mock(ReviewRequestDto.class);

        Review review = new Review(mockUser1, mockUser2, mockPost, mockRequestDto);

        //when
        when(mockRequestDto.getRate()).thenReturn(BigDecimal.ONE);
        lenient().when(mockUser1.getId()).thenReturn(userId1);
        lenient().when(mockUser2.getId()).thenReturn(userId2);
        lenient().when(mockPost.getId()).thenReturn(postId);

        when(userRepository.findById(userId1)).thenReturn(Optional.of(mockUser1));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(mockUser2));
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));

        when(reviewRepository.existsByReviewerAndRatedUserAndPost(mockUser1, mockUser2, mockPost)).thenReturn(false);

        when(reviewRepository.save(Mockito.any(Review.class))).thenReturn(review);
        ReviewResponseDto responseDto = reviewService.createReview(userId1, userId2, postId, mockRequestDto);

        //then
        assertNotNull(responseDto);
        assertEquals(mockRequestDto.getRate(), responseDto.getRate());
        assertEquals(userId2, responseDto.getRatedUserId());
        assertEquals(postId, responseDto.getPostId());
    }

    @Test
    @DisplayName("자기 자신을 리뷰하려 할 때 예외 발생")
    void 자기자신_리뷰_예외() {
        // given
        Long userId = 1L;
        Long postId = 1L;
        ReviewRequestDto mockRequestDto = Mockito.mock(ReviewRequestDto.class);

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
        Long userId = 1L;
        Long ratedUserId = 2L;
        Long postId = 1L;

        User mockReviewer = Mockito.mock(User.class);
        User mockRated = Mockito.mock(User.class);
        Post mockPost = Mockito.mock(Post.class);

        // when
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockReviewer));
        when(userRepository.findById(ratedUserId)).thenReturn(Optional.of(mockRated));
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));

        when(reviewRepository.existsByReviewerAndRatedUserAndPost(mockReviewer, mockRated, mockPost)).thenReturn(true);

        ReviewRequestDto mockRequestDto = Mockito.mock(ReviewRequestDto.class);

        //  then
        BaseException exception = assertThrows(BaseException.class, () ->
                reviewService.createReview(userId, ratedUserId, postId, mockRequestDto)
        );

        assertEquals(ErrorCode.ALREADY_REVIEWED.getStatus(), exception.getStatus());
        assertEquals(ErrorCode.ALREADY_REVIEWED.getMessage(), exception.getMessage());
    }
}