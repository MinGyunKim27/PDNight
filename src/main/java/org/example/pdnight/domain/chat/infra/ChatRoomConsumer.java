package org.example.pdnight.domain.chat.infra;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.pdnight.domain.chat.application.chatRoomUseCase.ChatRoomConsumerService;
import org.example.pdnight.global.event.PostConfirmedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatRoomConsumer {
    private final ChatRoomConsumerService chatRoomConsumerService;
    // 모임 성사
    @KafkaListener(topics = "post.confirmed", groupId = "chatroom-group")
    public void consumePostConfirmedEvent(PostConfirmedEvent event) {
        chatRoomConsumerService.handlePostConfirmed(event);
    }

    @KafkaListener(
            topics = {
                    "post.confirmed.DLT",
            },
            groupId = "DLT-handler",
            containerFactory = "dltListenerContainerFactory"
    )
    public void handleDLT(ConsumerRecord<String, Object> record) {
        chatRoomConsumerService.handlePostConfirmed((PostConfirmedEvent) record.value());
    }
}
