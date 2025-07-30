package org.example.pdnight.domain1.eventParticipant.repository;


import jakarta.persistence.LockModeType;
import org.example.pdnight.domain1.event.entity.Event;
import org.example.pdnight.domain1.eventParticipant.entity.EventParticipant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    @Query("SELECT COUNT(ep) FROM EventParticipant ep WHERE ep.event.id = :eventId")
    int getEventParticipantByEventId(Long eventId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT COUNT(ep) FROM EventParticipant ep WHERE ep.event.id = :eventId")
    int getEventParticipantByEventIdWithLock(Long eventId);

    @Query("SELECT ep FROM EventParticipant ep JOIN FETCH ep.user WHERE ep.event = :event")
    Page<EventParticipant> findByEventWithUser(Event event, Pageable pageable);
}
