package org.example.pdnight.domain.chat.domain;


import java.util.List;
import java.util.Optional;

public interface ChatRoomReader {
    List<ChatRoom> findAll();

    Optional<ChatRoom> findById(Long roomId);
}
