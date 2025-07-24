package org.example.pdnight.domain.eventParticipant.repository;


import org.example.pdnight.domain.eventParticipant.entity.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    boolean existsByEventIdAndUserId(Long eventId, Long userId);
}
