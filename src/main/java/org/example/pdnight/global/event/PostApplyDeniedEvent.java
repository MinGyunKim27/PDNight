package org.example.pdnight.global.event;

public record PostApplyDeniedEvent(
        Long postId,
        Long authorId,
        Long applierId
) {
}
