package org.example.pdnight.domain.event.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.event.entity.Event;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class EventResponse {
    private long id;
    private String title;
    private String content;
    private int maxParticipants;
    private LocalDateTime eventDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public EventResponse(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.content = event.getContent();
        this.maxParticipants = event.getMaxParticipants();
        this.eventDate = event.getEventDate();
        this.createdAt = event.getCreatedAt();
        this.updatedAt = event.getUpdatedAt();
    }
}
