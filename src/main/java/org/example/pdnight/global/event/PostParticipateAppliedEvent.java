package org.example.pdnight.global.event;

public record PostParticipateAppliedEvent(
        Long postId,
        Long authorId,
        Long applierId
) {
}
