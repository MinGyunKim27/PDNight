package org.example.pdnight.domain.chatRoom.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.chatRoom.dto.ChatMessageDto;
import org.example.pdnight.domain.chatRoom.dto.ChatRoomResponseDto;
import org.example.pdnight.domain.chatRoom.dto.NicknameResponseDto;
import org.example.pdnight.domain.chatRoom.entity.ChatRoom;
import org.example.pdnight.domain.chatRoom.enums.MessageType;
import org.example.pdnight.domain.chatRoom.service.ChattingService;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChattingRoomController {

    private final ChattingService chattingService;

    // 모든 채팅방 목록 반환
    @GetMapping("/chat/list")
    @ResponseBody
    public List<ChatRoomResponseDto> room() {
        return chattingService.list();
    }

    // 채팅방 생성
    @PostMapping("/chat/room")
    @ResponseBody
    public ChatRoom createRoom(@RequestParam String name) {
        return chattingService.create(name);
    }

    // 특정 채팅방 정보 조회
    @GetMapping("/chat/room/{roomId}")
    @ResponseBody
    public ChatRoomResponseDto roomInfo(@PathVariable Long roomId) {
        return chattingService.roomInfo(roomId);
    }

    // 채팅방 접속시 닉네임 정보 조회
    @GetMapping("/chat/enter/me")
    @ResponseBody
    public ResponseEntity<NicknameResponseDto> nicknameInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(new NicknameResponseDto(userDetails.getUsername()));
    }

    // 채팅방 입장 시도
    @GetMapping("/chatRoom/enter/{chatRoomId}")
    public ResponseEntity<ApiResponse<Void>> PostChatRoomEnter(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                               @PathVariable Long chatRoomId) {
        Long userId = userDetails.getUserId();
        String result = chattingService.chatRoomEnter(userId, chatRoomId);
        return ResponseEntity.ok(ApiResponse.ok(result, null));
    }

    // 채팅방 기록 조회
    @GetMapping("/chat/record")
    public List<ChatMessageDto> messageRecord(@RequestParam String roomId) {
        return chattingService.messageRecord(roomId);
    }

    // 채팅방 입장시 입장 메시지
    @MessageMapping("/chat/message")
    public void message(ChatMessageDto message) {
        if (MessageType.ENTER.equals(message.getMessageType())) {
            chattingService.enterChatRoom(message.getRoomId());
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        }
        chattingService.sendMessage(message);
    }
}
