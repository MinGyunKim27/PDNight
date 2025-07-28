package org.example.pdnight.domain.chatRoom.repository;

import org.example.pdnight.domain.chatRoom.entity.ChatRoom;
import org.example.pdnight.domain.chatRoom.entity.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatRoomParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    boolean existsByChatRoomAndUserId(ChatRoom chatRoom, Long userId);
}
