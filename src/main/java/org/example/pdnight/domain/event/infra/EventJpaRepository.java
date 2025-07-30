package org.example.pdnight.domain.event.infra;

import org.example.pdnight.domain.event.domain.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventJpaRepository extends JpaRepository<Event, Long>{
}


/* ------------ 이벤트 참가 레포지토리----------
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
 */
