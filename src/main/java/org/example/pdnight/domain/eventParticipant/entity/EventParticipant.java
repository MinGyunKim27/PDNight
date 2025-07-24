package org.example.pdnight.domain.eventParticipant.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.event.entity.Event;
import org.example.pdnight.domain.user.entity.User;

@Entity
@Table(name = "event_participants")
@Getter
@NoArgsConstructor
public class EventParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public EventParticipant(Event event, User user) {
        this.event = event;
        this.user = user;
    }
}
