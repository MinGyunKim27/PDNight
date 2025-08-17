package org.example.pdnight.domain.outbox.application;

public interface OutboxService {
    void saveOutboxEvent(String aggregateType, Long aggregateId, String eventType, Object payloadObject);
}
