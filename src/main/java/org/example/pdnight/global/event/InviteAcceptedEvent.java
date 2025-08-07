package org.example.pdnight.global.event;

public record InviteAcceptedEvent(
        Long authorId,
        Long userId,
        Long postId
) {
}
