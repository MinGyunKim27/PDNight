package org.example.pdnight.domain1.eventParticipant.dto.response;

import lombok.Getter;
import org.example.pdnight.domain1.eventParticipant.entity.EventParticipant;
import org.example.pdnight.domain1.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class EventParticipantResponse {
    private Long id;
    private Long eventId;
    private List<User> user = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private EventParticipantResponse(EventParticipant eventParticipant) {
        this.id = eventParticipant.getId();
        this.eventId = eventParticipant.getEvent().getId();
        this.user.add(eventParticipant.getUser());
        this.createdAt = eventParticipant.getCreatedAt();
        this.updatedAt = eventParticipant.getUpdatedAt();
    }

    public static EventParticipantResponse from(EventParticipant eventParticipant) {
        return new EventParticipantResponse(eventParticipant);
    }
}
