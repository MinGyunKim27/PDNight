package org.example.pdnight.domain.user.infra.userInfra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.example.pdnight.domain.user.application.userUseCase.UserConsumerService;
import org.example.pdnight.global.event.AuthDeletedEvent;
import org.example.pdnight.global.event.AuthSignedUpEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserConsumer {
    private final UserConsumerService userConsumerService;

    @KafkaListener(topics = "auth.signedup", groupId = "user-group")
    public void consumeAuthSignedUpEvent(AuthSignedUpEvent event) {
        try {
            userConsumerService.consumeAuthSignedUpEvent(event);
        } catch (Exception e) {
            log.error("UserConsumer : consumeAuthSignedUpEvent 리스너 에러 : " + e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(topics = "auth.deleted", groupId = "user-group")
    public void HandleAuthDelete(AuthDeletedEvent event) {

        try {
            userConsumerService.handleAuthDelete(event);
        } catch (Exception e) {
            log.error("UserConsumer : consumeAuthSignedUpEvent 리스너 에러 : " + e.getMessage(), e);
            throw e;
        }
    }


    private final KafkaTemplate kafkaTemplate;

    /*
     * 일시적인 서버 장애용 DLT 자동 재시도 로직
     *
     *
     * */
    @KafkaListener(
            topics = {
                    "auth.signedup.DLT",
                    "auth.deleted.DLT"
            },
            groupId = "DLT-handler"
    )
    public void RetryDLT(ConsumerRecord<String, Object> record) {

        String originalTopic = record.topic().replace(".DLT", "");
        String retryHeaderName = "x-retry-count";
        Headers headers = record.headers();
        int retryCount = 0;

        if (record.value() == null) {
            log.warn("DLT value가 null 포멧오류 또는 역직렬화 실패등");
            return;
        }

        Header retryHeader = headers.lastHeader(retryHeaderName);
        if (retryHeader != null) {
            retryCount = Integer.parseInt(new String(retryHeader.value(), StandardCharsets.UTF_8));
        }

        if (retryCount >= 3) {
            log.error("재시도 횟수 초과");
            return;
        }

        log.info("{} 번째 재시도", retryCount + 1);

        ProducerRecord<String, Object> retryRecord = new ProducerRecord<>(originalTopic, record.key(), record.value());
        retryRecord.headers().remove(retryHeaderName);
        retryRecord.headers().add(retryHeaderName, String.valueOf(retryCount + 1).getBytes(StandardCharsets.UTF_8));

        kafkaTemplate.send(retryRecord);

    }


}
