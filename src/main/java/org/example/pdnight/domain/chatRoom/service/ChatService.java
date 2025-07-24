package org.example.pdnight.domain.chatRoom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.chatRoom.dto.ChatRoomResponseDto;
import org.example.pdnight.domain.chatRoom.entity.ChatRoom;
import org.example.pdnight.domain.chatRoom.repository.ChatRoomRepository;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;


    public List<ChatRoomResponseDto> list() {
        return chatRoomRepository.findAll().stream()
                .map(chatRoom -> new ChatRoomResponseDto(chatRoom.getId(), chatRoom.getChatRoomName()))
                .toList();
    }

    public ChatRoom create(String name) {
        ChatRoom chatRoom = new ChatRoom(name);
        return chatRoomRepository.save(chatRoom);
    }

    public ChatRoomResponseDto roomInfo(Long roomId) {
        System.out.println("****check****" + roomId);
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new BaseException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        return new ChatRoomResponseDto(chatRoom.getId(), chatRoom.getChatRoomName());
    }
}
