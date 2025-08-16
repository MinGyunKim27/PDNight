package org.example.pdnight.domain.outbox.infra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.outbox.domain.OutboxEvent;
import org.example.pdnight.domain.outbox.enums.OutboxStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {
    private final OutboxJpaRepository outboxJpaRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    @Scheduled(fixedDelay = 3000)
    public void publish() {
        log.debug("[OutboxPublisher] 스케줄러 실행됨");

        List<OutboxEvent> events = outboxJpaRepository
                .findAllByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        log.debug("[OutboxPublisher] 조회된 PENDING 이벤트 수: {}", events.size());

        for (OutboxEvent event : events) {
            try {
                log.debug("[OutboxPublisher] 이벤트 발행 시도: id={}, topic={}, key={}",
                        event.getId(),
                        event.getAggregateType().toLowerCase(),
                        event.getAggregateId());

                kafkaTemplate.send(
                        event.getAggregateType().toLowerCase(),
                        event.getAggregateId(),
                        event.getPayload()
                ).get(); // 동기 발행

                event.setStatus(OutboxStatus.SENT);
                log.info("[OutboxPublisher] Kafka 발행 성공: id={}", event.getId());

            } catch (Exception e) {
                event.setStatus(OutboxStatus.FAILED);
                log.error("[OutboxPublisher] Kafka 발행 실패: id={}, message={}",
                        event.getId(), e.getMessage(), e);
            }
        }

        outboxJpaRepository.saveAll(events);
    }
}
