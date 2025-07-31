package org.example.pdnight.domain.chat.infra;

import org.example.pdnight.domain.chat.domain.ChatRoom;
import org.example.pdnight.domain.chat.domain.ChatRoomReader;

import java.util.Collection;
import java.util.Optional;

public class ChatRoomReaderImpl implements ChatRoomReader {
    private ChatRoomJpaRepository chatRoomJpaRepository;
    @Override
    public Collection<ChatRoom> findAll() {
        return chatRoomJpaRepository.findAll();
    }

    @Override
    public Boolean existsByPostId(Long postId) {
        return chatRoomJpaRepository.existsByPostId(postId);
    }

    @Override
    public Optional<ChatRoom> findById(Long roomId) {
        return chatRoomJpaRepository.findById(roomId);
    }
}
