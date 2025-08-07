package org.example.pdnight.domain.post.infra.post;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.application.commentUseCase.PostPort;
import org.example.pdnight.domain.post.domain.post.PostCommander;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostAdapter implements PostPort {

    private final PostCommander postCommander;

    @Override
    public boolean existsById(Long postId) {
        return postCommander.existsById(postId);
    }
}
