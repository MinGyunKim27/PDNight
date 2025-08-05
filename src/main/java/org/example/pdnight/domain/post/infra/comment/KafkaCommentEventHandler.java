package org.example.pdnight.domain.post.infra.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.post.application.commentUseCase.event.CommentEventHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaCommentEventHandler {
    private final CommentEventHandler commentEventHandler;

    @KafkaListener(topics = "post", groupId = "post-consumer-group1")
    public void consumer(String message) {
        try {
            log.info("Listen :: order : {}", message);
            Long postId = Long.parseLong(message.trim());
            commentEventHandler.handlerPostDeletedEvent(postId);
        } catch (Exception e) {
            log.error("Kafka consumer 처리 중 예외 발생. message = {}", message, e);
        }
    }
}
