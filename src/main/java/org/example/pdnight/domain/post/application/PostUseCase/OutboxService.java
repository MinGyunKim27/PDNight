package org.example.pdnight.domain.post.application.PostUseCase;

public interface OutboxService {
    public void saveOutboxEvent(String aggregateType, Long aggregateId, Object event);
}
