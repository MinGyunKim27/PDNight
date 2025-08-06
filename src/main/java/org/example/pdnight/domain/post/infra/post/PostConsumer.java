package org.example.pdnight.domain.post.infra.post;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.application.PostUseCase.PostConsumerService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostConsumer {

    private final PostConsumerService postConsumerService;

}
