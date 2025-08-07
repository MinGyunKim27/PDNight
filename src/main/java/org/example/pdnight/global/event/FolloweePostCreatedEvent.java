package org.example.pdnight.global.event;

import java.util.List;

public record FolloweePostCreatedEvent(
        Long authorId,
        Long postId,
        List<Long> followeeIds
) {
}
