package org.example.pdnight.domain.chat.application.chatMessageUseCase;


import org.example.pdnight.domain.chat.presentation.dto.request.ChatMessageDto;

import java.util.List;

public interface ChatMessageService {
    List<ChatMessageDto> messageRecord(String roomId);

    void sendMessage(ChatMessageDto message);
}
