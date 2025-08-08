package org.example.pdnight.domain.user.infra.userInfra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
//            throw new RuntimeException("예외던지기");
             userConsumerService.consumeAuthSignedUpEvent(event);
        } catch (Exception e) {
            log.error("UserConsumer : consumeAuthSignedUpEvent 리스너 에러 : "+ e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(topics = "auth.deleted", groupId = "user-group")
    public void HandleAuthDelete(AuthDeletedEvent event) {
        userConsumerService.handleAuthDelete(event);
    }

//    @KafkaListener(topics = "auth.signedup", groupId = "user-group")
//    public void consumeAuthSignedUpEvent2(AuthSignedUpEvent event) {
//        try {
//            throw new RuntimeException("예외던지기");
//            // userConsumerService.consumeAuthSignedUpEvent(event);
//        } catch (Exception e) {
//            log.error("UserConsumer : consumeAuthSignedUpEvent 리스너 에러 : "+ e.getMessage(), e);
//            throw e;
//        }
//    }

//    @KafkaListener(topics = "auth.deleted.DLT", groupId = "user-group", containerFactory = "eventContainerFactory")
//    public void HandleAuthDelete2(AuthDeletedEvent event) {
//        userConsumerService.handleAuthDelete(event);
//    }


}
