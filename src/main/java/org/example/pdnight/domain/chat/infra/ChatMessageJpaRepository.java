package org.example.pdnight.domain.chat.infra;

import org.example.pdnight.domain.chat.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageJpaRepository extends JpaRepository<ChatMessage, Long> {

}
