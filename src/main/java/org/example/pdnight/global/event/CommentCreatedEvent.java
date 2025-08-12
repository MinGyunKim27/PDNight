package org.example.pdnight.global.event;

public record CommentCreatedEvent(
        Long postAuthorId,
        Long commentAuthorId
) {
}
