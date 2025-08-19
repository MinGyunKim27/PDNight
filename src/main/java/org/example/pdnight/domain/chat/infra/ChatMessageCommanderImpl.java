package org.example.pdnight.domain.chat.infra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.chat.application.chatMessageUseCase.RedisSubscriber;
import org.example.pdnight.domain.chat.domain.ChatMessage;
import org.example.pdnight.domain.chat.domain.ChatMessageCommander;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class ChatMessageCommanderImpl implements ChatMessageCommander {
    private final ChatMessageJpaRepository chatMessageJpaRepository;
    private final RedisMessageListenerContainer redisMessageListener;
    private final RedisSubscriber redisSubscriber;
    private Map<String, ChannelTopic> topics = new ConcurrentHashMap<>();

    @Override
    public void save(ChatMessage chatMessage) {
        chatMessageJpaRepository.save(chatMessage);
    }

    @Override
    public void enterChatRoom(String roomId) {
        ChannelTopic topic = topics.computeIfAbsent(roomId, ChannelTopic::new);
        redisMessageListener.addMessageListener(redisSubscriber, topic);
    }

    @Override
    public ChannelTopic getTopic(String roomId) {
        return topics.get(roomId);
    }

}
