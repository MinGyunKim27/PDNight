package org.example.pdnight.domain.post.application.commentUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.post.domain.comment.Comment;
import org.example.pdnight.domain.post.domain.comment.CommentCommander;
import org.example.pdnight.domain.post.domain.comment.CommentProducer;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.post.presentation.dto.request.CommentRequest;
import org.example.pdnight.domain.post.presentation.dto.response.CommentResponse;
import org.example.pdnight.domain.post.presentation.dto.response.PostInfo;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.enums.KafkaTopic;
import org.example.pdnight.global.common.exception.BaseException;
import org.example.pdnight.global.event.CommentCreatedEvent;
import org.example.pdnight.global.event.CommentReplyCreatedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentCommanderService {

    private final CommentCommander commentCommander;
    private final PostPort postPort;
    private final CommentProducer producer;

    //댓글 생성 메서드
    public CommentResponse createComment(Long postId, Long loginId, CommentRequest request) {
        //게시글 존재하는지 검증
        validateIsExistPost(postId);

        //댓글 엔티티 생성 및 저장
        Comment comment = Comment.create(postId, loginId, request.getContent());
        Comment savedComment = commentCommander.save(comment);

        producer.produce(KafkaTopic.POST_COMMENT_CREATED.topicName(), new CommentCreatedEvent(postFromPort.getAuthorId(), comment.getAuthorId()));

        return CommentResponse.from(savedComment);
    }

    //댓글 삭제 메서드
    @Transactional
    public void deleteCommentById(Long postId, Long id, Long loginId) {
        //게시글 존재하는지 검증
        validateIsExistPost(postId);

        //댓글 검증 로직
        Comment foundComment = getCommentById(id);
        validateComment(loginId, postId, foundComment);

        //부모 댓글 id 기준 자식댓글 일괄 삭제 메서드
        commentCommander.deleteAllByParentId(id);
        commentCommander.delete(foundComment);
    }

    //댓글 수정 메서드
    @Transactional
    public CommentResponse updateCommentByDto(Long postId, Long id, Long loginId, CommentRequest request) {
        //게시글 존재하는지 검증
        validateIsExistPost(postId);

        //댓글 검증 로직
        Comment foundComment = getCommentById(id);
        validateComment(loginId, postId, foundComment);

        if (foundComment.getContent().equals(request.getContent())) {
            log.info("요청과 기존 댓글 내용이 동일하여 업데이트를 생략합니다. commentId = {}", foundComment.getId());
            return CommentResponse.from(foundComment);
        }

        foundComment.updateContent(request.getContent());
        return CommentResponse.from(foundComment);
    }

    //대댓글 생성 메서드
    public CommentResponse createChildComment(Long postId, Long id, Long loginId, CommentRequest request) {
        //게시글 존재하는지 검증
        validateIsExistPost(postId);

        Comment foundComment = getCommentById(id);

        //대댓글 엔티티 생성 및 저장
        Comment childComment = Comment.createChild(postId, loginId, request.getContent(), foundComment);
        Comment savedChildComment = commentCommander.save(childComment);

        producer.produce(KafkaTopic.POST_COMMENT_REPLY_CREATED.topicName(),  new CommentReplyCreatedEvent(foundComment.getAuthorId(), childComment.getAuthorId()));

        return CommentResponse.from(savedChildComment);
    }

    //어드민 권한 댓글 삭제 메서드
    @Transactional
    public void deleteCommentByAdmin(Long postId, Long id, Long adminId) {
        //게시글 존재하는지 검증
        validateIsExistPost(postId);

        //해당 댓글이 있는지 검증
        Comment foundComment = getCommentById(id);

        if (!foundComment.getPostId().equals(postId)) {
            throw new BaseException(ErrorCode.POST_NOT_MATCHED);
        }

        //부모 댓글 id 기준 자식댓글 일괄 삭제 메서드
        commentCommander.deleteAllByParentId(id);
        commentCommander.delete(foundComment);
        log.info("{}번 Id 관리자가 댓글을 삭제했습니다.", adminId);
    }

    //----------------------------------- HELPER 메서드 ------------------------------------------------------
    private Comment getCommentById(Long id) {
        return commentCommander.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.COMMENT_NOT_FOUND));
    }

    // validate
    private void validateIsExistPost(Long postId) {
        if (!postPort.existsById(postId)) {
            throw new BaseException(ErrorCode.POST_NOT_FOUND);
        }
    }

    private void validateComment(Long loginId, Long postId, Comment comment) {
        if (!comment.getAuthorId().equals(loginId)) {
            throw new BaseException(ErrorCode.COMMENT_FORBIDDEN);
        }

        if (!comment.getPostId().equals(postId)) {
            throw new BaseException(ErrorCode.POST_NOT_MATCHED);
        }
    }

}