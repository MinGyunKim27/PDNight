package org.example.pdnight.domain.chat.application.chatLogUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.chat.domain.ChatMessage;
import org.example.pdnight.domain.chat.domain.ChatMessageReader;
import org.example.pdnight.domain.chat.infra.ChatRoomRedisRepository;
import org.example.pdnight.domain.chat.presentation.dto.request.ChatMessageDto;


import java.util.List;

@RequiredArgsConstructor
public class ChatLogCommanderService {
    private ChatRoomRedisRepository chatRoomRedisRepository;

}
