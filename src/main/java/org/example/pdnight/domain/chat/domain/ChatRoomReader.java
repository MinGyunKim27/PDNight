package org.example.pdnight.domain.chat.domain;

import java.util.Collection;
import java.util.Optional;

public interface ChatRoomReader {
    Collection<ChatRoom> findAll();

    Boolean existsByPostId(Long postId);

    Optional<ChatRoom> findById(Long roomId);
}
