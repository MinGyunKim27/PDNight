package org.example.pdnight.domain.chatRoom.repository;

import org.example.pdnight.domain.chatRoom.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Boolean existsByPostId(Long postId);

    ChatRoom findByPostId(Long id);
}
