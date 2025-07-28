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
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<List<ChatRoomResponseDto>>> room() {
        return ResponseEntity.ok(ApiResponse.ok("채팅방 목록이 조회되었습니다.", chattingService.list()));
    }

    // 채팅방 생성
    @PostMapping("/chat/room")
    @ResponseBody
    public ResponseEntity<ApiResponse<ChatRoom>> createRoom(@RequestParam String name) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("채팅방이 생성되었습니다.", chattingService.create(name)));
    }

    // 특정 채팅방 정보 조회
    @GetMapping("/chat/room/{roomId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<ChatRoomResponseDto>> roomInfo(@PathVariable Long roomId) {
        return ResponseEntity.ok(ApiResponse.ok("채팅방 정보가 조회되었습니다.", chattingService.roomInfo(roomId)));
    }

    // 채팅방 접속시 닉네임 정보 조회
    @GetMapping("/chat/enter/me")
    @ResponseBody
    public ResponseEntity<ApiResponse<NicknameResponseDto>> nicknameInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok("닉네임이 조회되었습니다", NicknameResponseDto.from(userDetails.getUsername())));
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
    public ResponseEntity<ApiResponse<List<ChatMessageDto>>> messageRecord(@RequestParam String roomId) {
        return ResponseEntity.ok(ApiResponse.ok("채팅방 기록이 조회되었습니다.", chattingService.messageRecord(roomId)));
    }

    // 채팅 메시지 처리
    @MessageMapping("/chat/message")
    public ResponseEntity<ApiResponse<Void>> message(ChatMessageDto message) {
        if (MessageType.ENTER.equals(message.getMessageType())) {
            chattingService.enterChatRoom(message.getRoomId());
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        }
        chattingService.sendMessage(message);
        return ResponseEntity.ok(ApiResponse.ok("메시지가 전송되었습니다.", null));
    }
}
