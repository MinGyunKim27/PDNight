package org.example.pdnight.domain.chat.domain;

import org.springframework.data.redis.listener.ChannelTopic;

import java.util.Optional;

public interface ChatRoomCommandQuery {
    ChatRoom save(ChatRoom chatRoom);

    ChatRoom findByPostId(Long id);

    Optional<ChatRoom> findById(Long chatRoomId);

    ChannelTopic getTopic(String roomId);

    void enterChatRoom(String roomId);
}
