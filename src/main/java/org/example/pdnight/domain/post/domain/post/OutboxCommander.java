package org.example.pdnight.domain.post.domain.post;

import org.example.pdnight.domain.post.enums.OutboxStatus;

import java.util.List;

public interface OutboxCommander {
    List<OutboxEvent> findAllByStatusOrderByCreateAtAsc(OutboxStatus status);
}
