package org.example.pdnight.domain.chat.domain;

import org.springframework.data.redis.listener.ChannelTopic;

public interface ChatMessageCommander {
    ChannelTopic getTopic(String roomId);

    void enterChatRoom(String roomId);

    void save(ChatMessage chatMessage);
}
