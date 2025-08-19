package org.example.pdnight.domain.post.application.commentUseCase;

import org.example.pdnight.domain.post.domain.comment.Comment;
import org.example.pdnight.domain.post.domain.comment.CommentReader;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.post.presentation.dto.response.CommentResponse;
import org.example.pdnight.domain.post.presentation.dto.response.PostInfo;
import org.example.pdnight.global.common.dto.PagedResponse;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentReaderServiceTest {

    @Mock
    private CommentReader commentReader;

    @Mock
    private PostPort postPort;

    @InjectMocks
    private CommentReaderService commentReaderService;

    private PostInfo mockPostInfo;
    private Long postId;
    private Long authorId;

    @BeforeEach
    void setUp() {
        mockPostInfo = Mockito.mock(PostInfo.class);
        lenient().when(mockPostInfo.getPostId()).thenReturn(1L);
        lenient().when(mockPostInfo.getStatus()).thenReturn(PostStatus.OPEN);
    }

    @Test
    @DisplayName("댓글 다건조회 정상 반환 확인 테스트")
    void getCommentsByPostId_댓글_다건조회_정상구조() {
        //given
        postId = 1L;
        authorId = 1L;

        Pageable pageable = PageRequest.of(0, 10);

        when(postPort.existsByIdAndIsDeletedIsFalse(postId)).thenReturn(true);

        Comment parentComment1 = Comment.create(postId, authorId, "부모댓글1");
        Comment parentComment2 = Comment.create(postId, authorId, "부모댓글2");
        Comment childComment = Comment.createChild(postId, authorId, "자식댓글1", parentComment1);

        //Id 직접 세팅
        ReflectionTestUtils.setField(parentComment1, "id", 1L);
        ReflectionTestUtils.setField(parentComment2, "id", 2L);
        ReflectionTestUtils.setField(childComment, "id", 3L);

        //반환 리스트 세팅
        List<Comment> commentList = List.of(parentComment1, parentComment2, childComment);

        when(commentReader.findByPostIdOrderByIdAsc(postId)).thenReturn(commentList);

        //when
        PagedResponse<CommentResponse> response = commentReaderService.getCommentsByPostId(postId, pageable);

        //then
        List<CommentResponse> contents = response.contents();

        //부모댓글만 저장되니 size == 2
        assertEquals(2, contents.size());

        //1번 부모댓글엔 자식댓글 하나가 저장됐으니 size == 1
        assertEquals(1, contents.get(0).getChildren().size());

        //꺼낸 자식댓글의 아이디와 저장시킨 자식댓글 아이디 비교
        assertEquals(contents.get(0).getChildren().get(0).getId(), childComment.getId());

        verify(commentReader).findByPostIdOrderByIdAsc(postId);
    }

    @Test
    @DisplayName("게시글 없어서 예외 발생 테스트")
    void getCommentsByPostId_게시글_없어서_조회_실패() {
        //given
        postId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        when(postPort.existsByIdAndIsDeletedIsFalse(postId)).thenThrow(new BaseException(ErrorCode.POST_NOT_FOUND));

        //when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                commentReaderService.getCommentsByPostId(postId, pageable));

        assertEquals(ErrorCode.POST_NOT_FOUND.getMessage(), exception.getMessage());

        verify(postPort).existsByIdAndIsDeletedIsFalse(postId);
    }
}
