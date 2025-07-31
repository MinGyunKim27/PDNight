package org.example.pdnight.domain.chat.presentation.dto.response;

import lombok.Getter;

@Getter
public class ChatRoomResponseDto {
    private final Long id;
    private final String chatRoomName;

    private ChatRoomResponseDto(Long id, String chatRoomName) {
        this.id = id;
        this.chatRoomName = chatRoomName;
    }

    public static ChatRoomResponseDto create(Long id, String chatRoomName) {
        return new ChatRoomResponseDto(id, chatRoomName);
    }
}
