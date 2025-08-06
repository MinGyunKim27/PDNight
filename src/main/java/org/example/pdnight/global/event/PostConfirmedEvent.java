package org.example.pdnight.global.event;

import java.util.List;

public record PostConfirmedEvent(
        Long postId,
        Long authorId,
        List<Long> confirmedUserIds
) {
}
