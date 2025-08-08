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
//// 성공, 실패 한번에 컨트롤
//        send.handle((result, ex) -> {
//        if (ex != null) {
//        log.error("Kafka send failed", ex);
//            } else {
//                    log.info("Kafka sent. offset={}", result.getRecordMetadata().offset());
//        }
//        return null;
//        });
//
//        send.thenCompose(result -> {
////            전송데이터, 오프셋,파티션 같은 정보로 후속 작업
//        return null;
//        }).thenAccept(processResult -> {
//        log.info("후속 작업 완료 {}", processResult);
//        });