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
public class ChatRoomCommandImpl implements ChatRoomCommandQuery {
    private final ChatRoomJpaRepository chatRoomJpaRepository;
    private final ChatRoomRedisRepository chatRoomRedisRepository;
    @Override
    public Optional<ChatRoom> findById(Long chatRoomId) {
        return chatRoomJpaRepository.findById(chatRoomId);
    }

    @Override
    public ChannelTopic getTopic(String roomId) {
        return chatRoomRedisRepository.getTopic(roomId);
    }

    @Override
    public void enterChatRoom(String roomId) {
        chatRoomRedisRepository.enterChatRoom(roomId);
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
