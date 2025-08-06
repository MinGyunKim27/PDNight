package org.example.pdnight.domain.post.infra.consume;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.application.PostUseCase.event.PostConsumerService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostConsumer {

    private final PostConsumerService postConsumerService;

}
