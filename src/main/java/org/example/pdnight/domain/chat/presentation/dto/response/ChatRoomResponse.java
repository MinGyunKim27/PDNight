package org.example.pdnight.domain.chat.presentation.dto.response;

import lombok.Getter;

@Getter
public class ChatRoomResponse {
    private final Long id;
    private final String chatRoomName;

    private ChatRoomResponse(Long id, String chatRoomName) {
        this.id = id;
        this.chatRoomName = chatRoomName;
    }

    public static ChatRoomResponse create(Long id, String chatRoomName) {
        return new ChatRoomResponse(id, chatRoomName);
    }
}
