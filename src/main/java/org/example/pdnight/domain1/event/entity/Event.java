package org.example.pdnight.domain1.event.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain1.common.entity.Timestamped;

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

    protected Event(String title, String content, Integer maxParticipants, LocalDateTime eventDate) {
        this.title = title;
        this.content = content;
        this.maxParticipants = maxParticipants;
        this.eventDate = eventDate;
    }

    private Event(Long eventId, int i) {
        this.id = eventId;
        this.maxParticipants = i;
    }

    private Event(Long i, String title, int maxParticipants) {
        this.id = i;
        this.title = title;
        this.maxParticipants = maxParticipants;
    }

    public static Event from(Long eventId, int i) {
        return new Event(eventId, i);
    }

    public static Event from(String title, String content, Integer maxParticipants, LocalDateTime eventDate) {
        return new Event(title, content, maxParticipants, eventDate);
    }

    public static Event from(Long i, String title, int maxParticipants) {
        return new Event(i, title, maxParticipants);
    }

    public void updateEvent(String title, String content, Integer maxParticipants, LocalDateTime eventDate) {
        if(title != null) this.title = title;
        if(content != null) this.content = content;
        if(maxParticipants != null && maxParticipants >= 1) this.maxParticipants = maxParticipants;
        if(eventDate != null) this.eventDate = eventDate;
    }
}
