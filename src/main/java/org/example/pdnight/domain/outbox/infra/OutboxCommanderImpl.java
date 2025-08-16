package org.example.pdnight.domain.outbox.infra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.outbox.domain.OutboxCommander;
import org.example.pdnight.domain.outbox.domain.OutboxEvent;
import org.example.pdnight.domain.outbox.enums.OutboxStatus;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OutboxCommanderImpl implements OutboxCommander {

    private final OutboxJpaRepository outboxJpaRepository;

    @Override
    public void save(OutboxEvent outboxEvent) {
        outboxJpaRepository.save(outboxEvent);
    }

    @Override
    public List<OutboxEvent> findAllByStatusOrderByCreateAtAsc(OutboxStatus status) {
        return outboxJpaRepository.findAllByStatusOrderByCreatedAtAsc(status);
    }
}