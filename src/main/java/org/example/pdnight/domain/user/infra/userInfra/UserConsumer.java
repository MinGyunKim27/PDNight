package org.example.pdnight.domain.user.infra.userInfra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.application.userUseCase.UserConsumerService;
import org.example.pdnight.global.event.AuthDeletedEvent;
import org.example.pdnight.global.event.AuthSignedUpEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConsumer {
    private final UserConsumerService userConsumerService;

    @KafkaListener(topics = "auth.signedup", groupId = "user-group")
    public void consumeAuthSignedUpEvent(AuthSignedUpEvent event) {
        userConsumerService.consumeAuthSignedUpEvent(event);
    }

    @KafkaListener(topics = "auth.deleted", groupId = "user-group")
    public void HandleAuthDelete(AuthDeletedEvent event) {
        userConsumerService.handleAuthDelete(event);
    }
}
