package org.example.pdnight.domain.post.infra.post;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.post.OutboxEvent;
import org.example.pdnight.domain.post.enums.OutboxStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {
    private final OutboxJpaRepository outboxJpaRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    @Scheduled(fixedDelay = 3000)
    public void publish(){
        List<OutboxEvent> events = outboxJpaRepository.findAllByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        for(OutboxEvent event : events){
            try{
                kafkaTemplate.send(event.getAggregateType().toLowerCase(), event.getAggregateId().toString(), event.getPayload());
                event.setStatus(OutboxStatus.SENT);
            } catch (Exception e){
                event.setStatus(OutboxStatus.FAILED);
            }
        }
    }
}
