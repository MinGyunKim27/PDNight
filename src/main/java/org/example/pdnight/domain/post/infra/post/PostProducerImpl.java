package org.example.pdnight.domain.post.infra.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.post.domain.post.PostProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostProducerImpl implements PostProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void produce(final String topic, final Object message) {
        CompletableFuture<SendResult<String, Object>> send = kafkaTemplate.send(topic, message);
        // 전송 성공 시
        send.thenAccept(result -> {
                    log.info("Kafka send success: {}", result.getRecordMetadata());
                })      //전송 실패 시
                .exceptionally(ex -> {
                    log.error("Kafka send failed", ex);
                    return null;
                });


    }
}