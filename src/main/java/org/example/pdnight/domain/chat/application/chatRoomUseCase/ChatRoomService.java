package org.example.pdnight.domain.chat.application.chatRoomUseCase;


import org.example.pdnight.domain.chat.domain.ChatRoom;
import org.example.pdnight.domain.chat.presentation.dto.request.ChatMessageDto;
import org.example.pdnight.domain.chat.presentation.dto.response.ChatRoomResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ChatRoomService {
    List<ChatRoomResponseDto> list();

    ChatRoom create(String name);

    ChatRoomResponseDto roomInfo(Long roomId);

    String chatRoomEnterValid(Long userId, Long chatRoomId);

    void createFromPost(Long id);
}
