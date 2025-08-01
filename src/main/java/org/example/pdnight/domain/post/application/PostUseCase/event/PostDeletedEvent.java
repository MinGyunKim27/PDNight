package org.example.pdnight.domain.post.application.PostUseCase.event;

import lombok.Getter;

@Getter
public class PostDeletedEvent {
    private final Long postId;

    private PostDeletedEvent (Long postId) {
        this.postId = postId;
    }

    public static PostDeletedEvent of(Long postId) {
        return new PostDeletedEvent(postId);
    }
}
