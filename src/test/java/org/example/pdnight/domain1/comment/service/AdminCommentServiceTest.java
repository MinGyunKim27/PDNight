package org.example.pdnight.domain1.comment.service;

import org.example.pdnight.domain1.comment.entity.Comment;
import org.example.pdnight.domain1.comment.repository.CommentRepository;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;
import org.example.pdnight.domain1.post.entity.Post;
import org.example.pdnight.domain1.post.repository.PostRepository;
import org.example.pdnight.domain1.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private AdminCommentService adminCommentService;

    @Test
    @DisplayName("댓글 정상 삭제 테스트")
    void deleteCommentByAdmin_댓글_삭제_성공() {
        //given
        Post mockPost = Mockito.mock(Post.class);
        lenient().when(mockPost.getId()).thenReturn(1L);
        User mockUser = Mockito.mock(User.class);

        Long postId = 1L;
        Long commentId = 1L;
        Long adminId = 1L;

        String content = "댓글내용";
        Comment comment = Comment.create(mockPost, mockUser, content);
        ReflectionTestUtils.setField(comment, "id", 1L);

        when(postRepository.existsById(postId)).thenReturn(true);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        //when
        adminCommentService.deleteCommentByAdmin(postId, commentId, adminId);

        verify(postRepository).existsById(postId);
        verify(commentRepository).findById(commentId);
        verify(commentRepository).deleteAllByParentId(comment.getId());
        verify(commentRepository).delete(comment);
    }

    @Test
    @DisplayName("게시글이 없어 예외 발생 테스트")
    void deleteCommentByAdmin_게시글_없어_예외() {
        //given
        Long postId = 1L;
        Long commentId = 1L;
        Long adminId = 1L;

        when(postRepository.existsById(postId)).thenReturn(false);

        //when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                adminCommentService.deleteCommentByAdmin(postId, commentId, adminId));

        assertEquals(ErrorCode.POST_NOT_FOUND.getMessage(), exception.getMessage());

        verify(postRepository).existsById(postId);
    }

    @Test
    @DisplayName("댓글이 해당 게시글에 달린 댓글이 아니라 예외")
    void deleteCommentByAdmin_댓글과_게시글_일치하지_않아_예외() {
        //given
        Post mockPost = Mockito.mock(Post.class);
        lenient().when(mockPost.getId()).thenReturn(2L);
        User mockUser = Mockito.mock(User.class);

        Long postId = 1L;
        Long commentId = 1L;
        Long adminId = 1L;

        String content = "댓글내용";
        Comment comment = Comment.create(mockPost, mockUser, content);
        ReflectionTestUtils.setField(comment, "id", 1L);

        when(postRepository.existsById(postId)).thenReturn(true);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        //when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                adminCommentService.deleteCommentByAdmin(postId, commentId, adminId));

        assertEquals(ErrorCode.POST_NOT_MATCHED.getMessage(), exception.getMessage());

        verify(postRepository).existsById(postId);
        verify(commentRepository).findById(commentId);
    }

}