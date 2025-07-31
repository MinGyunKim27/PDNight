package org.example.pdnight.domain.event.presentation.dto.response;

import lombok.Getter;
import org.example.pdnight.domain.event.domain.entity.EventParticipant;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class EventParticipantResponse {
    private Long id;
    private Long eventId;
    private List<Long> user = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private EventParticipantResponse(EventParticipant eventParticipant) {
        this.id = eventParticipant.getId();
        this.eventId = eventParticipant.getEvent().getId();
        this.user.add(eventParticipant.getUserId());
        this.createdAt = eventParticipant.getCreatedAt();
        this.updatedAt = eventParticipant.getUpdatedAt();
    }

    public static EventParticipantResponse from(EventParticipant eventParticipant) {
        return new EventParticipantResponse(eventParticipant);
    }
}
