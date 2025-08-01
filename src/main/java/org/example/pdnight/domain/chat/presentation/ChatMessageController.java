package org.example.pdnight.domain.chat.presentation;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.chat.application.chatMessageUseCase.ChatMessageService;
import org.example.pdnight.domain.chat.enums.MessageType;
import org.example.pdnight.domain.chat.presentation.dto.request.ChatMessage;
import org.example.pdnight.global.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatMessageService chatLogService;

    // 채팅 메시지 처리
    @MessageMapping("/chat/message")
    public ResponseEntity<ApiResponse<Void>> message(ChatMessage message) {
        if (MessageType.ENTER.equals(message.getMessageType())) {
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        }
        chatLogService.sendMessage(message);
        return ResponseEntity.ok(ApiResponse.ok("메시지가 전송되었습니다.", null));
    }

    // 채팅방 기록 조회
    @GetMapping("/chat/record")
    public ResponseEntity<ApiResponse<List<ChatMessage>>> messageRecord(@RequestParam String roomId) {
        return ResponseEntity.ok(ApiResponse.ok("채팅방 기록이 조회되었습니다.", chatLogService.messageRecord(roomId)));
    }
}
