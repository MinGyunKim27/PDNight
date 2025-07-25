package org.example.pdnight.domain.chatRoom.repository;

import org.example.pdnight.domain.chatRoom.entity.Chatting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChattingRepository extends JpaRepository<Chatting, Long> {
    List<Chatting> findByRoomId(String roomId);
}
