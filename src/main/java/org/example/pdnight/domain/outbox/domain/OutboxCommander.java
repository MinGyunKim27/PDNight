package org.example.pdnight.domain.outbox.domain;

import org.example.pdnight.domain.outbox.enums.OutboxStatus;

import java.util.List;

public interface OutboxCommander {
    void save(OutboxEvent outboxEvent);
    List<OutboxEvent> findAllByStatusOrderByCreateAtAsc(OutboxStatus status);
}
