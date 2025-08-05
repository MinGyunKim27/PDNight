package org.example.pdnight.domain.post.infra.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.post.application.PostUseCase.event.PostEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostEventPublisher implements PostEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void PostDeletedEvent(Long postId) {
        kafkaTemplate.send("post", String.valueOf(postId));
        log.info("Publish :::::  {} :::::::::: {}", "post", postId);
    }
}
