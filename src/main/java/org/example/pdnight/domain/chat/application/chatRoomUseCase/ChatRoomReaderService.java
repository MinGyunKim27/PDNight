package org.example.pdnight.domain.chat.application.chatRoomUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.chat.domain.ChatRoom;
import org.example.pdnight.domain.chat.domain.ChatRoomReader;
import org.example.pdnight.domain.chat.presentation.dto.response.ChatRoomResponse;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
@RequiredArgsConstructor
public class ChatRoomReaderService {
    private final ChatRoomReader chatRoomReader;

    // 채팅방 목록 조회
    public List<ChatRoomResponse> list() {
        return chatRoomReader.findAll().stream()
                .map(chatRoom -> ChatRoomResponse.create(chatRoom.getId(), chatRoom.getChatRoomName()))
                .toList();
    }

    // 채팅방 정보 조회
    public ChatRoomResponse roomInfo(Long roomId) {
        ChatRoom chatRoom = chatRoomReader.findById(roomId).orElseThrow(() -> new BaseException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        return ChatRoomResponse.create(chatRoom.getId(), chatRoom.getChatRoomName());
    }
}
