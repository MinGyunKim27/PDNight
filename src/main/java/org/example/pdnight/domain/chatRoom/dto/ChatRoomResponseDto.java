package org.example.pdnight.domain.chatRoom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomResponseDto {
    private final Long id;
    private final String chatRoomName;

    public static ChatRoomResponseDto from(Long id, String chatRoomName) {
        return new ChatRoomResponseDto(id, chatRoomName);
    }
}
