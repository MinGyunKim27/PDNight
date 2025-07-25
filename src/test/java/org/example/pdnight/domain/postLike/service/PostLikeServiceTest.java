package org.example.pdnight.domain.postLike.service;

import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.repository.PostRepository;
import org.example.pdnight.domain.postLike.dto.response.PostLikeResponse;
import org.example.pdnight.domain.postLike.entity.PostLike;
import org.example.pdnight.domain.postLike.repository.PostLikeRepository;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostLikeServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostLikeRepository postLikeRepository;

    @InjectMocks
    private PostLikeService postLikeService;

    @Test
    @DisplayName("좋아요 등록 성공")
    void 좋아요_등록_성공() {
        // given
        Long userId = 1L;
        Long postId = 1L;

        User mockUser = Mockito.mock(User.class);
        Post mockPost = Mockito.mock(Post.class);
        PostLike mockPostLike = new PostLike(mockPost, mockUser);

        // when
        lenient().when(mockUser.getId()).thenReturn(userId);
        lenient().when(mockPost.getId()).thenReturn(postId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
        when(postLikeRepository.existsByPostAndUser(mockPost, mockUser)).thenReturn(false);
        when(postLikeRepository.save(Mockito.any(PostLike.class))).thenReturn(mockPostLike);


        PostLikeResponse response = postLikeService.addLike(postId, userId);

        // then
        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals(postId, response.getPostId());
    }

    @Test
    @DisplayName("이미 좋아요를 누른 경우 예외 발생")
    void 중복_좋아요_예외() {
        // given
        Long userId = 1L;
        Long postId = 10L;

        User mockUser = Mockito.mock(User.class);
        Post mockPost = Mockito.mock(Post.class);


        // when & then
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
        when(postLikeRepository.existsByPostAndUser(mockPost, mockUser)).thenReturn(true);

        BaseException exception = assertThrows(BaseException.class, () ->
                postLikeService.addLike(postId, userId)
        );

        assertEquals(ErrorCode.ALREADY_LIKED.getStatus(), exception.getStatus());
        assertEquals(ErrorCode.ALREADY_LIKED.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("좋아요 취소 성공")
    void 좋아요_취소_성공() {
        // given
        Long userId = 1L;
        Long postId = 10L;

        User mockUser = Mockito.mock(User.class);
        Post mockPost = Mockito.mock(Post.class);
        PostLike mockPostLike = new PostLike(mockPost, mockUser);

        // when
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
        when(postLikeRepository.findByPostAndUser(mockPost, mockUser)).thenReturn(Optional.of(mockPostLike));


        assertDoesNotThrow(() -> postLikeService.removeLike(postId, userId));

        // then
        verify(postLikeRepository).delete(mockPostLike);
    }

    @Test
    @DisplayName("좋아요 기록이 존재하지 않을 때 예외 발생")
    void 좋아요_취소_좋아요없음_예외() {
        // given
        Long userId = 1L;
        Long postId = 10L;

        User mockUser = Mockito.mock(User.class);
        Post mockPost = Mockito.mock(Post.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
        when(postLikeRepository.findByPostAndUser(mockPost, mockUser)).thenReturn(Optional.empty());

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                postLikeService.removeLike(postId, userId)
        );

        assertEquals(ErrorCode.POSTLIKE_NOT_FOUND.getStatus(), exception.getStatus());
    }
}