package org.example.pdnight.domain.chat.infra;

import org.example.pdnight.domain.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomJpaRepository extends JpaRepository<ChatRoom, Long> {

    ChatRoom findByPostId(Long id);
}
