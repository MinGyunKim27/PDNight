package org.example.pdnight.domain.post.application.commentUseCase;

import org.springframework.stereotype.Component;

@Component
public interface PostPort {

    boolean existsById(Long postId);

}
