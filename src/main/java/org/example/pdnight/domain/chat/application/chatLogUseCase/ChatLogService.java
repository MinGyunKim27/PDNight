package org.example.pdnight.domain.chat.application.chatLogUseCase;


import org.example.pdnight.domain.chat.presentation.dto.request.ChatMessageDto;

import java.util.List;

public interface ChatLogService {
    List<ChatMessageDto> messageRecord(String roomId);
}
