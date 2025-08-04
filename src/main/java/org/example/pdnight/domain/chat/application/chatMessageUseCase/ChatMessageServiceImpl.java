package org.example.pdnight.domain.chat.application.chatMessageUseCase;


import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.chat.presentation.dto.request.ChatMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {
    private final ChatMessageReaderService chatMessageReaderService;
    private final ChatMessageCommanderService chatMessageCommanderService;

    @Override
    public List<ChatMessage> messageRecord(String roomId) {
        return chatMessageReaderService.messageRecord(roomId);
    }

    @Override
    public void sendMessage(ChatMessage message) {
        chatMessageCommanderService.sendMessage(message);
    }

}
