package org.example.pdnight.global.event;

import java.util.List;

public record ChatroomCreatedEvent(
        List<Long> participantIds,
        Long authId
) {

}
