package org.example.pdnight.domain.chat.application.chatMessageUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.chat.domain.ChatMessageReader;
import org.example.pdnight.domain.chat.presentation.dto.request.ChatMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageReaderService {
    private final ChatMessageReader chatMessageReader;
    // 채팅방 기록 출력
    public List<ChatMessage> messageRecord(String roomId) {
        List<org.example.pdnight.domain.chat.domain.ChatMessage> messages = chatMessageReader.findByRoomId(roomId);
        return messages.stream().map(ChatMessage::from).toList();
    }
}
