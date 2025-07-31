package org.example.pdnight.domain.chat.application.chatLogUseCase;


import org.example.pdnight.domain.chat.presentation.dto.request.ChatMessageDto;

import java.util.List;

public class ChatLogServiceImpl implements ChatLogService{
    @Override
    public List<ChatMessageDto> messageRecord(String roomId) {
        return List.of();
    }
}
