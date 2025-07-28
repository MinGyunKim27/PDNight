package org.example.pdnight.domain.event.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.common.entity.Timestamped;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer maxParticipants;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    public Event(String title, String content, Integer maxParticipants, LocalDateTime eventDate) {
        this.title = title;
        this.content = content;
        this.maxParticipants = maxParticipants;
        this.eventDate = eventDate;
    }

    public Event(Long eventId, int i) {
        this.id = eventId;
        this.maxParticipants = i;
    }

    public void updateEvent(String title, String content, Integer maxParticipants, LocalDateTime eventDate) {
        if(title != null) this.title = title;
        if(content != null) this.content = content;
        if(maxParticipants != null && maxParticipants >= 1) this.maxParticipants = maxParticipants;
        if(eventDate != null) this.eventDate = eventDate;
    }
}
