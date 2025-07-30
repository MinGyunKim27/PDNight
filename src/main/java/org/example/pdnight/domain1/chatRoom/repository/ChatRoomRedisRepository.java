package org.example.pdnight.domain1.chatRoom.repository;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain1.chatRoom.service.RedisSubscriber;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ChatRoomRedisRepository {
    private final RedisMessageListenerContainer redisMessageListener;
    private final RedisSubscriber redisSubscriber;
    private Map<String, ChannelTopic> topics = new ConcurrentHashMap<>();

    public void enterChatRoom(String roomId) {
        ChannelTopic topic = topics.computeIfAbsent(roomId, ChannelTopic::new);
        redisMessageListener.addMessageListener(redisSubscriber, topic);
    }

    public ChannelTopic getTopic(String roomId) {
        return topics.get(roomId);
    }
}
