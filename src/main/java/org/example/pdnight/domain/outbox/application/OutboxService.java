package org.example.pdnight.domain.outbox.application;

public interface OutboxService {
    public void saveOutboxEvent(String aggregateType, Long aggregateId, String eventType, Object payloadObject);
}
