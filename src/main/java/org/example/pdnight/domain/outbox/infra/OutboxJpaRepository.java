package org.example.pdnight.domain.outbox.infra;

import org.example.pdnight.domain.outbox.domain.OutboxEvent;
import org.example.pdnight.domain.outbox.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxJpaRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findAllByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
