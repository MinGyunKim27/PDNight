package org.example.pdnight.global.event;

public record InviteDeniedEvent(
        Long authorId,
        Long userId,
        Long postId
) {
}
