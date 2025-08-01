package org.example.pdnight.domain.chat.application.chatRoomUseCase;


import org.example.pdnight.domain.chat.domain.ChatRoom;
import org.example.pdnight.domain.chat.presentation.dto.response.ChatRoomResponse;

import java.util.List;

public interface ChatRoomService {
    List<ChatRoomResponse> list();

    ChatRoom create(String name);

    ChatRoomResponse roomInfo(Long roomId);

    String chatRoomEnterValid(Long userId, Long chatRoomId);

    void createFromPost(Long id);
}
