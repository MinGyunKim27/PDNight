package org.example.pdnight.domain.post.infra.post;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.application.PostUseCase.PostConsumerService;
import org.example.pdnight.global.event.AuthDeletedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostConsumer {

    private final PostConsumerService postConsumerService;

    @KafkaListener(topics = "auth.deleted", groupId = "post-group")
    public void HandleAuthDelete(AuthDeletedEvent event){
        postConsumerService.handleAuthDelete(event);
    }

}
