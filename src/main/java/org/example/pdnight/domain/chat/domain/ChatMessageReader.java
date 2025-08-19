package org.example.pdnight.domain.chat.domain;


import java.util.List;

public interface ChatMessageReader {
    List<ChatMessage> findByRoomId(String roomId);
}
