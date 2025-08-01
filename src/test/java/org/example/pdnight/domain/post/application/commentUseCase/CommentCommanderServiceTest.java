package org.example.pdnight.domain.post.application.commentUseCase;

import org.example.pdnight.domain.post.application.port.PostPort;
import org.example.pdnight.domain.post.domain.comment.Comment;
import org.example.pdnight.domain.post.domain.comment.CommentCommander;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.post.presentation.dto.request.CommentRequest;
import org.example.pdnight.domain.post.presentation.dto.response.CommentResponse;
import org.example.pdnight.domain.post.presentation.dto.response.PostInfo;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentCommanderServiceTest {

    @Mock
    private CommentCommander commentCommander;

    @Mock
    private PostPort postPort;

    @InjectMocks
    private CommentCommanderService commentCommanderService;

    private PostInfo mockPostInfo;
    private Long postId;
    private Long authorId;
    private Long commentId;

    @BeforeEach
    void setUp() {
        mockPostInfo = Mockito.mock(PostInfo.class);
        lenient().when(mockPostInfo.getPostId()).thenReturn(1L);
        lenient().when(mockPostInfo.getStatus()).thenReturn(PostStatus.OPEN);
    }

    @Test
    @DisplayName("댓글 정상 생성 테스트")
    void createComment_댓글_생성_성공() {
        //given
        postId = 1L;
        authorId = 1L;
        String content = "댓글내용";
        CommentRequest request = Mockito.mock(CommentRequest.class);
        lenient().when(request.getContent()).thenReturn(content);

        Comment comment = Comment.create(postId, authorId, request.getContent());

        when(postPort.findById(postId)).thenReturn(mockPostInfo);
        when(mockPostInfo.getStatus()).thenReturn(PostStatus.OPEN);
        when(commentCommander.save(any(Comment.class))).thenReturn(comment);

        //when
        CommentResponse result = commentCommanderService.createComment(postId, authorId, request);

        //then
        assertEquals(authorId, result.getAuthorId());
        assertEquals(mockPostInfo.getPostId(), result.getPostId());
        assertEquals(content, result.getContent());

        verify(postPort).findById(postId);
        verify(commentCommander).save(any(Comment.class));
    }

    @Test
    @DisplayName("게시물이 없어서 예외 발생 테스트")
    void createComment_게시물_없음_생성_실패() {
        //given
        postId = 1L;
        authorId = 1L;
        CommentRequest request = Mockito.mock(CommentRequest.class);

        when(postPort.findById(postId)).thenThrow(new BaseException(ErrorCode.POST_NOT_FOUND));

        //when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                commentCommanderService.createComment(postId, authorId, request));

        assertEquals(ErrorCode.POST_NOT_FOUND.getMessage(), exception.getMessage());

        verify(postPort).findById(postId);
    }

    @Test
    @DisplayName("댓글 정상 삭제 테스트")
    void deleteCommentById_댓글_삭제_성공() {
        //given
        postId = 1L;
        commentId = 1L;
        authorId = 1L;

        String content = "댓글내용";
        Comment comment = Comment.create(postId, authorId, content);
        ReflectionTestUtils.setField(comment, "id", 1L);

        when(postPort.existsById(postId)).thenReturn(true);
        when(commentCommander.findById(commentId)).thenReturn(Optional.of(comment));

        //when & then
        commentCommanderService.deleteCommentById(postId, commentId, authorId);

        verify(postPort).existsById(postId);
        verify(commentCommander).findById(commentId);
        verify(commentCommander).deleteAllByParentId(comment.getId());
        verify(commentCommander).delete(comment);
    }

    @Test
    @DisplayName("댓글 작성자가 아니라 삭제 시 예외 발생 테스트")
    void deleteCommentById_작성자가_아닌_댓글_삭제_실패() {
        //given
        postId = 1L;
        commentId = 1L;
        authorId = 1L;
        Long loginId = 10L;
        String content = "댓글내용";
        Comment comment = Comment.create(postId, authorId, content);
        ReflectionTestUtils.setField(comment, "id", 1L);

        when(postPort.existsById(postId)).thenReturn(true);
        when(commentCommander.findById(commentId)).thenReturn(Optional.of(comment));

        //when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                commentCommanderService.deleteCommentById(postId, commentId, loginId));

        assertEquals(ErrorCode.COMMENT_FORBIDDEN.getMessage(), exception.getMessage());

        verify(postPort).existsById(postId);
        verify(commentCommander).findById(commentId);
    }

    @Test
    @DisplayName("댓글 정상 수정 테스트")
    void updateCommentByDto_댓글_수정_성공() {
        //given
        postId = 1L;
        commentId = 1L;
        authorId = 1L;
        String oldContent = "댓글내용";
        Comment comment = Comment.create(postId, authorId, oldContent);
        CommentRequest request = Mockito.mock(CommentRequest.class);
        lenient().when(request.getContent()).thenReturn("수정된내용");

        when(postPort.existsById(postId)).thenReturn(true);
        when(commentCommander.findById(commentId)).thenReturn(Optional.of(comment));

        //when
        CommentResponse response = commentCommanderService.updateCommentByDto(postId, commentId, authorId, request);

        //then
        assertEquals(request.getContent(), response.getContent());
        assertNotEquals(oldContent, response.getContent());
        assertEquals("수정된내용", comment.getContent());

        verify(postPort).existsById(postId);
        verify(commentCommander).findById(commentId);
    }

    @Test
    @DisplayName("댓글 작성자가 아니라 수정 시 예외 발생 테스트")
    void updateCommentByDto_작성자가_아닌_댓글_수정_실패() {
        //given
        postId = 1L;
        commentId = 1L;
        authorId = 1L;
        Long loginId = 10L;
        String oldContent = "댓글내용";
        Comment comment = Comment.create(postId, authorId, oldContent);
        CommentRequest request = Mockito.mock(CommentRequest.class);
        lenient().when(request.getContent()).thenReturn("수정된내용");

        when(postPort.existsById(postId)).thenReturn(true);
        when(commentCommander.findById(commentId)).thenReturn(Optional.of(comment));

        //when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                commentCommanderService.updateCommentByDto(postId, commentId, loginId, request));

        assertEquals(ErrorCode.COMMENT_FORBIDDEN.getMessage(), exception.getMessage());

        verify(postPort).existsById(postId);
        verify(commentCommander).findById(commentId);
    }

    @Test
    @DisplayName("대댓글 정상 생성 테스트")
    void createChildComment_대댓글_생성_성공() {
        //given
        postId = 1L;
        commentId = 1L;
        authorId = 1L;
        String content = "댓글내용";
        CommentRequest request = Mockito.mock(CommentRequest.class);
        lenient().when(request.getContent()).thenReturn(content);

        Comment parentComment = Comment.create(postId, authorId, request.getContent());
        ReflectionTestUtils.setField(parentComment, "id", 1L);

        Comment childComment = Comment.createChild(postId, authorId, request.getContent(), parentComment);
        ReflectionTestUtils.setField(childComment, "id", 2L);

        when(postPort.findById(postId)).thenReturn(mockPostInfo);
        when(mockPostInfo.getStatus()).thenReturn(PostStatus.OPEN);
        when(commentCommander.findById(commentId)).thenReturn(Optional.of(parentComment));
        when(commentCommander.save(any(Comment.class))).thenReturn(childComment);

        //when
        CommentResponse result = commentCommanderService.createChildComment(postId, commentId, authorId, request);

        //then
        assertEquals(authorId, result.getAuthorId());
        assertEquals(postId, result.getPostId());
        assertEquals(content, result.getContent());
        assertEquals(parentComment.getId(), result.getParentId());

        verify(postPort).findById(postId);
        verify(commentCommander).findById(commentId);
        verify(commentCommander).save(any(Comment.class));
    }

    @Test
    @DisplayName("관리자 댓글 정상 삭제 테스트")
    void deleteCommentByAdmin_관리자_댓글_삭제_성공() {
        //given
        postId = 1L;
        commentId = 1L;
        authorId = 1L;
        Long adminId = 3L;

        String content = "댓글내용";
        Comment comment = Comment.create(postId, authorId, content);
        ReflectionTestUtils.setField(comment, "id", 1L);

        when(postPort.existsById(postId)).thenReturn(true);
        when(commentCommander.findById(commentId)).thenReturn(Optional.of(comment));

        //when & then
        commentCommanderService.deleteCommentByAdmin(postId, commentId, adminId);

        verify(postPort).existsById(postId);
        verify(commentCommander).findById(commentId);
        verify(commentCommander).deleteAllByParentId(comment.getId());
        verify(commentCommander).delete(comment);
    }

    @Test
    @DisplayName("게시글이 없어 예외 발생 테스트")
    void deleteCommentByAdmin_게시글_없어_예외() {
        //given
        postId = 1L;
        commentId = 1L;
        authorId = 1L;
        Long adminId = 3L;

        when(postPort.existsById(postId)).thenReturn(false);

        //when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                commentCommanderService.deleteCommentByAdmin(postId, commentId, adminId));

        assertEquals(ErrorCode.POST_NOT_FOUND.getMessage(), exception.getMessage());

        verify(postPort).existsById(postId);
        verifyNoMoreInteractions(commentCommander);
    }

    @Test
    @DisplayName("댓글이 해당 게시글에 달린 댓글이 아니라 예외")
    void deleteCommentByAdmin_댓글과_게시글_일치하지_않아_예외() {
        //given
        postId = 1L;
        commentId = 1L;
        authorId = 1L;
        Long anotherPostId = 10L;
        Long adminId = 3L;

        String content = "댓글내용";
        Comment comment = Comment.create(postId, authorId, content);
        ReflectionTestUtils.setField(comment, "id", 1L);

        when(postPort.existsById(anotherPostId)).thenReturn(true);
        when(commentCommander.findById(commentId)).thenReturn(Optional.of(comment));

        //when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                commentCommanderService.deleteCommentByAdmin(anotherPostId, commentId, adminId));

        assertEquals(ErrorCode.POST_NOT_MATCHED.getMessage(), exception.getMessage());

        verify(postPort).existsById(anotherPostId);
        verify(commentCommander).findById(commentId);
        verifyNoMoreInteractions(commentCommander);
    }

}