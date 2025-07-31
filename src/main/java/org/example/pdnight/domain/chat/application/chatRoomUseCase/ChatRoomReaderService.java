package org.example.pdnight.domain.chat.application.chatRoomUseCase;

import org.example.pdnight.domain.chat.domain.ChatRoom;
import org.example.pdnight.domain.chat.domain.ChatRoomReader;
import org.example.pdnight.domain.chat.presentation.dto.response.ChatRoomResponseDto;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;


import java.util.List;

public class ChatRoomReaderService {
    private ChatRoomReader chatRoomReader;
    // 게시글 채팅방이 생성되었는지 확인
    public Boolean checkPostChatRoom(Long postId) {
        return chatRoomReader.existsByPostId(postId);
    }

    // 채팅방 목록 조회
    public List<ChatRoomResponseDto> list() {
        return chatRoomReader.findAll().stream()
                .map(chatRoom -> ChatRoomResponseDto.create(chatRoom.getId(), chatRoom.getChatRoomName()))
                .toList();
    }

    // 채팅방 정보 조회
    public ChatRoomResponseDto roomInfo(Long roomId) {
        ChatRoom chatRoom = chatRoomReader.findById(roomId).orElseThrow(() -> new BaseException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        return ChatRoomResponseDto.create(chatRoom.getId(), chatRoom.getChatRoomName());
    }
}
