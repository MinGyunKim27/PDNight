package org.example.pdnight.domain.post.application.commentUseCase.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.post.application.PostUseCase.event.PostDeletedEvent;
import org.example.pdnight.domain.post.domain.comment.CommentCommander;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
public class CommentEventListener {

    private final CommentCommander commentCommander;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlerPostDeletedEvent(PostDeletedEvent event) {
        try {
            commentCommander.deleteAllByPostId(event.getPostId());
            commentCommander.deleteAllByChildrenPostId(event.getPostId());
            log.info("댓글이 정상적으로 삭제되었습니다. postId = {}", event.getPostId());
        } catch (Exception e) {
            log.error("댓글 삭제에 실패했습니다. postId = {}", event.getPostId());
        }
    }

}