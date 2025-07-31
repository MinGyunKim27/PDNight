package org.example.pdnight.domain.event.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.global.common.entity.Timestamped;

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

    @Column(nullable = false)
    private Long userId;

    private EventParticipant(Event event, Long userId) {
        this.event = event;
        this.userId = userId;
    }

    public static EventParticipant create(Event event, Long userId) {
        return new EventParticipant(event, userId);
    }

}
