package org.example.pdnight.domain.chat.presentation;

import org.example.pdnight.domain.chat.application.chatLogUseCase.ChatLogService;
import org.example.pdnight.domain.chat.presentation.dto.request.ChatMessageDto;
import org.example.pdnight.global.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class ChatMessageController {
    private ChatLogService chatLogService;
    // 채팅방 기록 조회
    @GetMapping("/chat/record")
    public ResponseEntity<ApiResponse<List<ChatMessageDto>>> messageRecord(@RequestParam String roomId) {
        return ResponseEntity.ok(ApiResponse.ok("채팅방 기록이 조회되었습니다.", chatLogService.messageRecord(roomId)));
    }
}
