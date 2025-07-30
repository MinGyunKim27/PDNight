package org.example.pdnight.domain1.chatRoom.repository;

import org.example.pdnight.domain1.chatRoom.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChattingRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByRoomId(String roomId);
}
