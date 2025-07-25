package org.example.pdnight.domain.chatRoom.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.chatRoom.dto.ChatMessageDto;
import org.example.pdnight.domain.chatRoom.enums.MessageType;
import org.example.pdnight.domain.chatRoom.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;


@RequiredArgsConstructor
@Controller
public class ChatController {
    private final ChatService chatService;

    // 채팅방 입장시 입장 메시지
    @MessageMapping("/chat/message")
    public void message(ChatMessageDto message) {
        if (MessageType.ENTER.equals(message.getMessageType())) {
            chatService.enterChatRoom(message.getRoomId());
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        }
        chatService.sendMessage(message);
    }
}
