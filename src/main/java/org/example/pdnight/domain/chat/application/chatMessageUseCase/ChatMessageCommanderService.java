package org.example.pdnight.domain.chat.application.chatMessageUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.chat.domain.ChatMessage;
import org.example.pdnight.domain.chat.domain.ChatMessageCommander;
import org.example.pdnight.domain.chat.presentation.dto.request.ChatMessageDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageCommanderService {
    private final ChatMessageCommander chatMessageCommandQuery;

    // 메시지 보내기
    public void sendMessage(ChatMessageDto message) {
        // 메시지 기록
        ChatMessage chatMessage = ChatMessage.from(message.getRoomId(), message.getSender(), message.getMessage(), message.getMessageType());
        chatMessageCommandQuery.save(chatMessage);

        ChannelTopic topic = chatMessageCommandQuery.getTopic(message.getRoomId());
        topic = getOrSubscribeTopic(topic, message);
        publish(topic, message);
    }

    private ChannelTopic getOrSubscribeTopic(ChannelTopic topic, ChatMessageDto message) {
        // 토픽이 없으면 구독 등록 후 다시 가져오기
        if (topic == null) {
            chatMessageCommandQuery.enterChatRoom(message.getRoomId());
            topic = chatMessageCommandQuery.getTopic(message.getRoomId());
        }
        return topic;
    }


    // ---------------- redis publisher --------------
    private final RedisTemplate<String, Object> redisTemplate;

    // 메시지를 발행
    public void publish(ChannelTopic topic, ChatMessageDto message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }

}
