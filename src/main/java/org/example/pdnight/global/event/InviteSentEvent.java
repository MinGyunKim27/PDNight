package org.example.pdnight.global.event;

public record InviteSentEvent(
        Long authorId,
        Long userId,
        Long postId
) {
}
