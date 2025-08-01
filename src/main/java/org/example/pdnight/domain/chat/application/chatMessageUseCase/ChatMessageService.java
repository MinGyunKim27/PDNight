package org.example.pdnight.domain.chat.application.chatMessageUseCase;


import org.example.pdnight.domain.chat.presentation.dto.request.ChatMessage;

import java.util.List;

public interface ChatMessageService {
    List<ChatMessage> messageRecord(String roomId);

    void sendMessage(ChatMessage message);
}
