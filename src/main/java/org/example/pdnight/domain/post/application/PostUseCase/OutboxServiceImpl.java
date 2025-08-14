package org.example.pdnight.domain.post.application.PostUseCase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService{

    private final OutboxCommanderService outboxCommanderService;

    public void saveOutboxEvent(String aggregateType, Long aggregateId, Object event){
        outboxCommanderService.saveOutboxEvent(aggregateType, aggregateId, event);
    }
}
