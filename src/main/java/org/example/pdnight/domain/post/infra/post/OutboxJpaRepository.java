package org.example.pdnight.domain.post.infra.post;

import org.example.pdnight.domain.post.domain.post.OutboxEvent;
import org.example.pdnight.domain.post.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxJpaRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findAllByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
