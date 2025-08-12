package org.example.pdnight.global.event;

import lombok.Getter;

@Getter
public class PostDeletedEvent {
    private final Long postId;

    private PostDeletedEvent(Long postId) {
        this.postId = postId;
    }

    public static PostDeletedEvent of(Long postId) {
        return new PostDeletedEvent(postId);
    }
}
