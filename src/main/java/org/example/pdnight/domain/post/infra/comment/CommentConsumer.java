package org.example.pdnight.domain.post.infra.comment;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.application.commentUseCase.CommentConsumerService;
import org.example.pdnight.global.event.PostDeletedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentConsumer {
    private final CommentConsumerService consumerService;

    @KafkaListener(topics = "post.deleted", groupId = "alert-group")
    public void consumePostDeletedEvent(PostDeletedEvent event) {
        consumerService.handlePostDeletedEvent(event);
    }
}
