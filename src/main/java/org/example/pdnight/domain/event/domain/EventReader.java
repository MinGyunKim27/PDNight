package org.example.pdnight.domain.event.domain;

import org.example.pdnight.domain.event.domain.entity.Event;
import org.example.pdnight.domain.event.domain.entity.EventParticipant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface EventReader {

    Optional<Event> findById(Long id);

    Page<Event> findAllEvent(Pageable pageable);

    boolean existsEventByIdAndUserId(Long eventId, Long userId);

    Long getEventParticipantByEventId(Long eventId);

    Page<EventParticipant> findByEventWithUser(Event event, Pageable pageable);

}
