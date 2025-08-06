package org.example.pdnight.domain.post.application.PostUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.post.domain.post.PostCommander;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostConsumerService {

    private final PostCommander postCommander;

}
