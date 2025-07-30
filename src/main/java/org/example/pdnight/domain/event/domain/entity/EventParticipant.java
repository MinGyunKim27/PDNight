package org.example.pdnight.domain.event.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.common.entity.Timestamped;
import org.example.pdnight.domain.user.domain.entity.User;

@Entity
@Table(name = "event_participants")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventParticipant extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private EventParticipant(Event event, User user) {
        this.event = event;
        this.user = user;
    }

    public static EventParticipant create(Event event, User user) {
        return new EventParticipant(event, user);
    }

}
