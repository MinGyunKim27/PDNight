package org.example.pdnight.domain.eventParticipant.repository;


import org.example.pdnight.domain.eventParticipant.entity.EventParticipant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    @Query("SELECT ep FROM EventParticipant ep JOIN FETCH ep.user WHERE ep.event.id = :eventId")
    Page<EventParticipant> findByEventIdWithUser(@Param("eventId") Long eventId, Pageable pageable);

    @Query("SELECT COUNT(ep) FROM EventParticipant ep WHERE ep.event.id = :eventId")
    int getEventParticipantByEventId(Long eventId);
}
