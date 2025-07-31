package org.example.pdnight.domain.chat.domain;


import org.springframework.data.redis.listener.ChannelTopic;

import java.util.Optional;

public interface ChatRoomCommandQuery {
    ChatRoom save(ChatRoom chatRoom);

    ChatRoom findByPostId(Long id);

    void enterChatRoom(String roomId);

    ChannelTopic getTopic(String roomId);

    Optional<ChatRoom> findById(Long chatRoomId);

    boolean existsByChatRoomAndUserId(ChatRoom chatRoom, Long authorId);

    void saveParticipant(ChatParticipant chatRoomAuth);
}
