package org.example.pdnight.domain.notification.presentation.dto.event;

import java.util.List;

public record PostConfirmedEvent(
        Long postId,
        Long authorId,
        List<Long> confirmedUserIds
) {
}
