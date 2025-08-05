package org.example.pdnight.domain.post.application.commentUseCase.event;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.post.domain.comment.CommentCommander;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentEventHandler {
    private final CommentCommander commentCommander;

    @Transactional
    public void handlerPostDeletedEvent(Long postId) {
        commentCommander.deleteAllByChildrenPostId(postId);
        commentCommander.deleteAllByPostId(postId);
    }
}
