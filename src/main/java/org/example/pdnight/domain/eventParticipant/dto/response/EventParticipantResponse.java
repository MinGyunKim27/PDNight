package org.example.pdnight.domain.eventParticipant.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class EventParticipantResponse {
    private Long id;
    private String title;
    private String content;
    private Integer maxParticipants;
    private String eventDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
