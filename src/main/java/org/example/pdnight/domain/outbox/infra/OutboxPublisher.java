package org.example.pdnight.domain.outbox.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.notification.infra.ElasticsearchIndexService;
import org.example.pdnight.domain.outbox.domain.OutboxEvent;
import org.example.pdnight.domain.outbox.enums.OutboxStatus;
import org.example.pdnight.domain.post.domain.post.PostDocument;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxJpaRepository outboxJpaRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ElasticsearchIndexService elasticsearchIndexService;

    private static final int BATCH_SIZE = 10;

    @Transactional
    @Scheduled(fixedDelay = 5000)
    public void publish() {
        log.debug("[OutboxPublisher] 스케줄러 실행됨");

        // 트랜잭션 단위로 PENDING 이벤트 잠금
        List<OutboxEvent> events = fetchPendingEvents(BATCH_SIZE);
        if (events.isEmpty()) {
            log.debug("[OutboxPublisher] 처리할 이벤트 없음");
            return;
        }

        for (OutboxEvent event : events) {
            try {
                // 상태를 PROCESSING으로 미리 마킹
                markProcessing(event);

                boolean kafkaSuccess = sendToKafka(event);
                boolean esSuccess = sendToElasticsearch(event);

                if (kafkaSuccess && esSuccess) {
                    markSent(event);
                } else {
                    markFailed(event);
                }

            } catch (Exception e) {
                markFailed(event);
                log.error("[OutboxPublisher] 이벤트 처리 중 예외 발생: id={}, message={}",
                        event.getId(), e.getMessage(), e);
            }
        }

        // 상태 변경 DB 반영
        saveAll(events);
    }

    @Transactional
    protected List<OutboxEvent> fetchPendingEvents(int batchSize) {
        return outboxJpaRepository.findAllByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING)
                .stream()
                .limit(batchSize)
                .collect(Collectors.toList());
    }

    @Transactional
    protected void markProcessing(OutboxEvent event) {
        event.setStatus(OutboxStatus.PROCESSING);
        event.setUpdatedAt(LocalDateTime.now());
        outboxJpaRepository.save(event);
        log.debug("[OutboxPublisher] PROCESSING 상태로 마킹: id={}", event.getId());
    }

    protected boolean sendToKafka(OutboxEvent event) {
        try {
            // payload를 PostDocument로 역직렬화
            PostDocument postDocument = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .readValue(event.getPayload(), PostDocument.class);

            kafkaTemplate.send("post", postDocument).get(); // 동기 발행
            log.info("[OutboxPublisher] Kafka 발행 성공: id={}", event.getId());
            return true;
        } catch (Exception e) {
            log.error("[OutboxPublisher] Kafka 발행 실패: id={}, message={}",
                    event.getId(), e.getMessage(), e);
            return false;
        }
    }

    protected boolean sendToElasticsearch(OutboxEvent event) {
        try {
            // payload를 PostDocument로 역직렬화
            PostDocument postDocument = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .readValue(event.getPayload(), PostDocument.class);

            elasticsearchIndexService.indexPost(postDocument);
            log.info("[OutboxPublisher] Elasticsearch 전송 성공: id={}", event.getId());
            return true;
        } catch (Exception e) {
            log.error("[OutboxPublisher] Elasticsearch 전송 실패: id={}, message={}",
                    event.getId(), e.getMessage(), e);
            return false;
        }
    }

    @Transactional
    protected void markSent(OutboxEvent event) {
        event.markAsSent();
        outboxJpaRepository.save(event);
        log.debug("[OutboxPublisher] SENT 상태로 마킹: id={}", event.getId());
    }

    @Transactional
    protected void markFailed(OutboxEvent event) {
        event.markAsFailed();
        outboxJpaRepository.save(event);
        log.debug("[OutboxPublisher] FAILED 상태로 마킹: id={}", event.getId());
    }

    @Transactional
    protected void saveAll(List<OutboxEvent> events) {
        outboxJpaRepository.saveAll(events);
    }
}
