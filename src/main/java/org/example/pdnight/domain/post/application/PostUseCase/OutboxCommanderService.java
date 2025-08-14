package org.example.pdnight.domain.post.application.PostUseCase;

import org.springframework.stereotype.Service;

@Service
public class OutboxCommanderService {

    public void saveOutboxEvent(String aggregateType, Long aggregateId, Object event){

    }

}
