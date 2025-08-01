package org.example.pdnight.domain.chat.domain;

import java.util.Optional;

public interface ChatRoomCommander {
    ChatRoom save(ChatRoom chatRoom);

    ChatRoom findByPostId(Long id);

    Optional<ChatRoom> findById(Long chatRoomId);

}
