package org.example.pdnight.domain.post.infra.post;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.post.OutboxCommander;
import org.example.pdnight.domain.post.domain.post.OutboxEvent;
import org.example.pdnight.domain.post.enums.OutboxStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OutboxCommanderImpl implements OutboxCommander {

    private final OutboxJpaRepository outboxJpaRepository;

    @Override
    public List<OutboxEvent> findAllByStatusOrderByCreateAtAsc(OutboxStatus status) {
        return List.of();
    }
}
