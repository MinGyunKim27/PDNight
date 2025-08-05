package org.example.pdnight.domain.post.application.PostUseCase.event;

public interface PostEventPublisher {
    void PostDeletedEvent(Long postId);
}
