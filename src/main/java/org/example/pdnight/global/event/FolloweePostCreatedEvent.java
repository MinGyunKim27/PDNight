package org.example.pdnight.global.event;

public record FolloweePostCreatedEvent (
        Long authorId,
        Long postId
){
}
