package org.example.pdnight.domain.user.infra.userInfra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.pdnight.domain.user.application.userUseCase.UserConsumerService;
import org.example.pdnight.global.event.AuthDeletedEvent;
import org.example.pdnight.global.event.AuthSignedUpEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

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

    /*
     * 일시적인 DB서버 장애용 DLT 자동 재시도 로직
     *
     *
     * */
    @KafkaListener(
            topics = {
                    "auth.signedup.DLT",
                    "auth.deleted.DLT"
            },
            groupId = "DLT-handler",
            containerFactory = "dltListenerContainerFactory"
    )
    public void handleDLT(ConsumerRecord<String, Object> record) {
        if (record.topic().equals("auth.signedup.DLT")) {
            userConsumerService.consumeAuthSignedUpEvent((AuthSignedUpEvent) record.value());
        }

        if (record.topic().equals("auth.deleted.DLT")) {
            userConsumerService.handleAuthDelete((AuthDeletedEvent) record.value());
        }
    }

}