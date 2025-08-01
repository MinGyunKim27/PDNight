package org.example.pdnight.domain.event.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.global.common.entity.Timestamped;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;

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

    @OneToMany(mappedBy = "event", cascade = ALL, orphanRemoval = true)
    private List<EventParticipant> eventParticipants = new ArrayList<>();

    @Column(nullable = false)
    private Integer maxParticipants;

    @Column(nullable = false)
    private LocalDateTime eventStartDate;

    @Column(nullable = false)
    private LocalDateTime eventEndDate;

    protected Event(String title, String content, Integer maxParticipants, LocalDateTime eventStartDate, LocalDateTime eventEndDate) {
        this.title = title;
        this.content = content;
        this.maxParticipants = maxParticipants;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
    }

    private Event(Long eventId, int i) {
        this.id = eventId;
        this.maxParticipants = i;
    }

    private Event(Long id, String title, int maxParticipants) {
        this.id = id;
        this.title = title;
        this.maxParticipants = maxParticipants;
    }

    public static Event from(Long eventId, int id) {
        return new Event(eventId, id);
    }

    public static Event from(String title, String content, Integer maxParticipants, LocalDateTime eventStartDate, LocalDateTime eventEndDate) {
        return new Event(title, content, maxParticipants, eventStartDate, eventEndDate);
    }

    public static Event from(Long id, String title, int maxParticipants) {
        return new Event(id, title, maxParticipants);
    }

    public void updateEvent(String title, String content, Integer maxParticipants, LocalDateTime eventStartDate, LocalDateTime eventEndDate) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        if (maxParticipants != null && maxParticipants >= 1) this.maxParticipants = maxParticipants;
        if (eventStartDate != null) this.eventStartDate = eventStartDate;
        if (eventEndDate != null) this.eventEndDate = eventEndDate;
    }

    public void addParticipant(EventParticipant eventParticipant) {
        this.eventParticipants.add(eventParticipant);
    }
}
