package org.example.pdnight.domain.comment.service;

import org.example.pdnight.domain.comment.dto.request.CommentRequestDto;
import org.example.pdnight.domain.comment.dto.response.CommentResponseDto;
import org.example.pdnight.domain.comment.entity.Comment;
import org.example.pdnight.domain.comment.repository.CommentRepository;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.common.helper.GetHelper;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private GetHelper getHelper;

    @InjectMocks
    private CommentService commentService;

    private User mockUser;
    private Post mockPost;
    private Long postId;
    private Long authorId;
    private Long commentId;

    @BeforeEach
    void setUp() {
        mockUser = Mockito.mock(User.class);
        mockPost = Mockito.mock(Post.class);

        lenient().when(mockUser.getId()).thenReturn(1L);
        lenient().when(mockPost.getId()).thenReturn(1L);
    }

    @Test
    @DisplayName("댓글 정상 생성 테스트")
    void createComment_댓글_생성_성공() {
        //given
        postId = 1L;
        authorId = 1L;
        String content = "댓글내용";
        CommentRequestDto request = new CommentRequestDto(content);

        when(getHelper.getPostByIdOrElseThrow(postId)).thenReturn(mockPost);
        when(getHelper.getUserByIdOrElseThrow(authorId)).thenReturn(mockUser);

        Comment comment = Comment.create(mockPost, mockUser, request.getContent());
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        //when
        CommentResponseDto result = commentService.createComment(postId, authorId, request);

        //then
        assertEquals(result.getAuthorId(), mockUser.getId());
        assertEquals(result.getPostId(), mockPost.getId());
        assertEquals(result.getContent(), request.getContent());

        verify(getHelper).getPostByIdOrElseThrow(postId);
        verify(getHelper).getUserByIdOrElseThrow(authorId);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("게시물이 없어서 예외 발생 테스트")
    void createComment_게시물_없음_생성_실패() {
        //given
        postId = 1L;
        authorId = 1L;
        String content = "댓글내용";
        CommentRequestDto request = new CommentRequestDto(content);

        when(getHelper.getPostByIdOrElseThrow(postId)).thenThrow(new BaseException(ErrorCode.POST_NOT_FOUND));

        //when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                commentService.createComment(postId, authorId, request));

        assertEquals(ErrorCode.POST_NOT_FOUND.getMessage(), exception.getMessage());

        verify(getHelper).getPostByIdOrElseThrow(postId);
    }

    @Test
    @DisplayName("댓글 정상 삭제 테스트")
    void deleteCommentById_댓글_삭제_성공() {
        //given
        postId = 1L;
        commentId = 1L;
        authorId = 1L;

        String content = "댓글내용";
        Comment comment = Comment.create(mockPost, mockUser, content);
        ReflectionTestUtils.setField(comment, "id", 1L);

        when(getHelper.getPostByIdOrElseThrow(postId)).thenReturn(mockPost);
        when(getHelper.getUserByIdOrElseThrow(authorId)).thenReturn(mockUser);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        //when & then
        commentService.deleteCommentById(postId, commentId, authorId);

        verify(getHelper).getPostByIdOrElseThrow(postId);
        verify(getHelper).getUserByIdOrElseThrow(authorId);
        verify(commentRepository).findById(commentId);
        verify(commentRepository).deleteAllByParentId(comment.getId());
        verify(commentRepository).delete(comment);
    }

    @Test
    @DisplayName("댓글 작성자가 아니라 삭제 시 예외 발생 테스트")
    void deleteCommentById_작성자가_아닌_댓글_삭제_실패() {
        //given
        postId = 1L;
        commentId = 1L;
        authorId = 10L;
        String content = "댓글내용";
        Comment comment = Comment.create(mockPost, mockUser, content);

        User anotherUser = Mockito.mock(User.class);
        lenient().when(anotherUser.getId()).thenReturn(10L);

        when(getHelper.getPostByIdOrElseThrow(postId)).thenReturn(mockPost);
        when(getHelper.getUserByIdOrElseThrow(authorId)).thenReturn(mockUser);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        //when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                commentService.deleteCommentById(postId, commentId, authorId));

        assertEquals(ErrorCode.COMMENT_FORBIDDEN.getMessage(), exception.getMessage());

        verify(getHelper).getPostByIdOrElseThrow(postId);
        verify(getHelper).getUserByIdOrElseThrow(authorId);
        verify(commentRepository).findById(commentId);
    }

    @Test
    @DisplayName("댓글 정상 수정 테스트")
    void updateCommentByDto_댓글_수정_성공() {
        //given
        postId = 1L;
        commentId = 1L;
        authorId = 1L;
        String oldContent = "댓글내용";
        Comment comment = Comment.create(mockPost, mockUser, oldContent);
        CommentRequestDto request = new CommentRequestDto("수정할 댓글내용");

        when(getHelper.getPostByIdOrElseThrow(postId)).thenReturn(mockPost);
        when(getHelper.getUserByIdOrElseThrow(authorId)).thenReturn(mockUser);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        //when
        CommentResponseDto response = commentService.updateCommentByDto(postId, commentId, authorId, request);

        //then
        assertEquals(request.getContent(), response.getContent());
        assertNotEquals(oldContent, response.getContent());

        verify(getHelper).getPostByIdOrElseThrow(postId);
        verify(getHelper).getUserByIdOrElseThrow(authorId);
        verify(commentRepository).findById(commentId);
    }

    @Test
    @DisplayName("댓글 작성자가 아니라 수정 시 예외 발생 테스트")
    void updateCommentByDto_작성자가_아닌_댓글_수정_실패() {
        //given
        postId = 1L;
        commentId = 1L;
        authorId = 10L;
        String oldContent = "댓글내용";
        Comment comment = Comment.create(mockPost, mockUser, oldContent);
        CommentRequestDto request = new CommentRequestDto("수정할 댓글내용");

        User anotherUser = Mockito.mock(User.class);
        lenient().when(anotherUser.getId()).thenReturn(10L);

        when(getHelper.getPostByIdOrElseThrow(postId)).thenReturn(mockPost);
        when(getHelper.getUserByIdOrElseThrow(authorId)).thenReturn(mockUser);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        //when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                commentService.updateCommentByDto(postId, commentId, authorId, request));

        assertEquals(ErrorCode.COMMENT_FORBIDDEN.getMessage(), exception.getMessage());

        verify(getHelper).getPostByIdOrElseThrow(postId);
        verify(getHelper).getUserByIdOrElseThrow(authorId);
        verify(commentRepository).findById(commentId);
    }

    @Test
    @DisplayName("대댓글 정상 생성 테스트")
    void createChildComment_대댓글_생성_성공() {
        //given
        postId = 1L;
        commentId = 1L;
        authorId = 1L;
        String content = "댓글내용";
        CommentRequestDto request = new CommentRequestDto(content);

        Comment parentComment = Comment.create(mockPost, mockUser, request.getContent());
        Comment childComment = Comment.createChild(mockPost, mockUser, request.getContent(), parentComment);

        when(getHelper.getPostByIdOrElseThrow(postId)).thenReturn(mockPost);
        when(getHelper.getUserByIdOrElseThrow(authorId)).thenReturn(mockUser);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(parentComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(childComment);

        //when
        CommentResponseDto result = commentService.createChildComment(postId, commentId, authorId, request);

        //then
        assertEquals(result.getAuthorId(), mockUser.getId());
        assertEquals(result.getPostId(), mockPost.getId());
        assertEquals(result.getContent(), request.getContent());
        assertEquals(result.getParentId(), parentComment.getId());

        verify(getHelper).getPostByIdOrElseThrow(postId);
        verify(getHelper).getUserByIdOrElseThrow(authorId);
        verify(commentRepository).findById(commentId);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 다건조회 정상 반환 확인 테스트")
    void getCommentsByPostId_댓글_다건조회_정상구조() {
        //given
        postId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        when(getHelper.getPostByIdOrElseThrow(postId)).thenReturn(mockPost);

        Comment parentComment1 = Comment.create(mockPost, mockUser, "부모댓글1");
        Comment parentComment2 = Comment.create(mockPost, mockUser, "부모댓글2");
        Comment childComment = Comment.createChild(mockPost, mockUser, "자식댓글1", parentComment1);

        //Id 직접 세팅
        ReflectionTestUtils.setField(parentComment1, "id", 1L);
        ReflectionTestUtils.setField(parentComment2, "id", 2L);
        ReflectionTestUtils.setField(childComment, "id", 3L);

        //반환 리스트 세팅
        List<Comment> commentList = List.of(parentComment1, parentComment2, childComment);

        when(commentRepository.findByPostIdOrderByIdAsc(postId)).thenReturn(commentList);

        //when
        PagedResponse<CommentResponseDto> response = commentService.getCommentsByPostId(postId, pageable);

        //then
        List<CommentResponseDto> contents = response.contents();

        //부모댓글만 저장되니 size == 2
        assertEquals(2, contents.size());

        //1번 부모댓글엔 자식댓글 하나가 저장됐으니 size == 1
        assertEquals(1, contents.get(0).getChildren().size());

        //꺼낸 자식댓글의 아이디와 저장시킨 자식댓글 아이디 비교
        assertEquals(contents.get(0).getChildren().get(0).getId(), childComment.getId());

        verify(commentRepository).findByPostIdOrderByIdAsc(postId);
    }

    @Test
    @DisplayName("게시글 없어서 예외 발생 테스트")
    void getCommentsByPostId_게시글_없어서_조회_실패() {
        //given
        postId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        when(getHelper.getPostByIdOrElseThrow(postId)).thenThrow(new BaseException(ErrorCode.POST_NOT_FOUND));

        //when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                commentService.getCommentsByPostId(postId, pageable));

        assertEquals(ErrorCode.POST_NOT_FOUND.getMessage(), exception.getMessage());

        verify(getHelper).getPostByIdOrElseThrow(postId);
    }
}
