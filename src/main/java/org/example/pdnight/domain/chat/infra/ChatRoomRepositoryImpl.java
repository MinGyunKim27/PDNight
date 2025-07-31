package org.example.pdnight.domain.chat.infra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.chat.application.chatRoomUseCase.RedisSubscriber;
import org.example.pdnight.domain.chat.domain.ChatParticipant;
import org.example.pdnight.domain.chat.domain.ChatRoom;
import org.example.pdnight.domain.chat.domain.ChatRoomCommandQuery;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomCommandQuery {
    private ChatRoomJpaRepository chatRoomJpaRepository;
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

    @Override
    public Optional<ChatRoom> findById(Long chatRoomId) {
        return chatRoomJpaRepository.findById(chatRoomId);
    }

    @Override
    public boolean existsByChatRoomAndUserId(ChatRoom chatRoom, Long authorId) {
        return true;
//        chatRoomJpaRepository.findByParticipantsByChat;
    }

    @Override
    public void saveParticipant(ChatParticipant chatRoomAuth) {

    }

    @Override
    public ChatRoom save(ChatRoom chatRoom) {
        return chatRoomJpaRepository.save(chatRoom);
    }

    @Override
    public ChatRoom findByPostId(Long id) {
        return chatRoomJpaRepository.findByPostId(id);
    }
}
