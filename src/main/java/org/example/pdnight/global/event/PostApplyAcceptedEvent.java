package org.example.pdnight.global.event;

public record PostApplyAcceptedEvent(
        Long postId,
        Long authorId,
        Long applierId
) {
}
