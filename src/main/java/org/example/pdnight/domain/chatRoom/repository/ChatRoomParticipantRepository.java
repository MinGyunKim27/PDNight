package org.example.pdnight.domain.chatRoom.repository;

import org.example.pdnight.domain.chatRoom.entity.ChatRoom;
import org.example.pdnight.domain.chatRoom.entity.ChatRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {
    boolean existsByChatRoomAndUserId(ChatRoom chatRoom, Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);
}
