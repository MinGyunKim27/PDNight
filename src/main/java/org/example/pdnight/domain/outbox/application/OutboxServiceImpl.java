package org.example.pdnight.domain.outbox.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService{

    private final OutboxCommanderService outboxCommanderService;

    public void saveOutboxEvent(String aggregateType, Long aggregateId, String eventType, Object payloadObject){
        outboxCommanderService.saveOutboxEvent(aggregateType, aggregateId, eventType, payloadObject);
    }
}
