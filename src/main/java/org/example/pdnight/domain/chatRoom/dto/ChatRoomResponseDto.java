package org.example.pdnight.domain.chatRoom.dto;

import lombok.Getter;

@Getter
public class ChatRoomResponseDto {
    private final Long id;
    private final String chatRoomName;

    public ChatRoomResponseDto(Long id, String chatRoomName) {
        this.id = id;
        this.chatRoomName = chatRoomName;
    }
}
