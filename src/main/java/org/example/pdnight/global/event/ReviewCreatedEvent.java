package org.example.pdnight.global.event;

public record ReviewCreatedEvent (
        Long reviewerId,
        Long revieweeId
) {
}
