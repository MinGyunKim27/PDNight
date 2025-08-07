package org.example.pdnight.domain.post.application.commentUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.post.domain.comment.CommentCommander;
import org.example.pdnight.global.event.PostDeletedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentConsumerService {

    private final CommentCommander commentCommander;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handlePostDeletedEvent(PostDeletedEvent event) {
        try {
            commentCommander.deleteAllByChildrenPostId(event.getPostId());
            commentCommander.deleteAllByPostId(event.getPostId());
            log.info("댓글이 정상적으로 삭제되었습니다. postId = {}", event.getPostId());
        } catch (Exception e) {
            log.error("댓글 삭제에 실패했습니다. postId = {}", event.getPostId());
        }
    }
}