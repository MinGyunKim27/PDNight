package org.example.pdnight.domain.chat.infra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.chat.domain.ChatRoom;
import org.example.pdnight.domain.chat.domain.ChatRoomCommander;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatRoomCommandImpl implements ChatRoomCommander {
    private final ChatRoomJpaRepository chatRoomJpaRepository;

    @Override
    public Optional<ChatRoom> findById(Long chatRoomId) {
        return chatRoomJpaRepository.findById(chatRoomId);
    }

    @Override
    public ChatRoom save(ChatRoom chatRoom) {
        return chatRoomJpaRepository.save(chatRoom);
    }

    @Override
    public ChatRoom findByPostId(Long id) {
        return chatRoomJpaRepository.findByPostId(id);
    }
}
