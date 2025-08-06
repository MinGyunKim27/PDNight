package org.example.pdnight.domain.user.infra.userInfra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.application.userUseCase.UserConsumerService;
import org.example.pdnight.global.event.AuthSignedUpEvent;
import org.springframework.kafka.annotation.KafkaListener;

@RequiredArgsConstructor
public class UserConsumer {
    private final UserConsumerService userConsumerService;

    @KafkaListener(topics = "post.participant.confirmed", groupId = "alert-group")
    public void consumeAuthSignedUpEvent(AuthSignedUpEvent event) {
        userConsumerService.consumeAuthSignedUpEvent(event);
    }
}
