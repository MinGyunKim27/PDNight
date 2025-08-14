package org.example.pdnight.domain.chat.presentation;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.chat.application.chatRoomUseCase.ChatRoomService;
import org.example.pdnight.domain.chat.domain.ChatRoom;
import org.example.pdnight.domain.chat.presentation.dto.response.ChatRoomResponse;
import org.example.pdnight.domain.chat.presentation.dto.response.NicknameResponse;
import org.example.pdnight.global.common.dto.ApiResponse;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    // 로그인 화면
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // 채팅 리스트 화면
    @GetMapping("/chat/view")
    public String rooms() {
        return "chat/room";
    }

    // 채팅방 입장 화면
    @GetMapping("/chat/view/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "chat/roomdetail";
    }

    // 모든 채팅방 목록 반환
    @GetMapping("/chat/list")
    @ResponseBody
    @Operation(summary = "채팅방 목록 조회", description = "채팅방 목록을 조회합니다", tags = {"Chat"})
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> room() {
        return ResponseEntity.ok(ApiResponse.ok("채팅방 목록이 조회되었습니다.", chatRoomService.list()));
    }

    // 채팅방 생성
    @PostMapping("/chat/room")
    @ResponseBody
    @Operation(summary = "채팅방 생성", description = "채팅방을 생성합니다", tags = {"Chat"})
    public ResponseEntity<ApiResponse<ChatRoomResponse>> createRoom(@RequestParam String name) {
        ChatRoom chatRoom = chatRoomService.create(name);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse
                .ok("채팅방이 생성되었습니다.", ChatRoomResponse.create(chatRoom.getId(), chatRoom.getChatRoomName())));
    }

    // 특정 채팅방 정보 조회
    @GetMapping("/chat/room/{roomId}")
    @ResponseBody
    @Operation(summary = "채팅방 정보 조회", description = "채팅방 정보를 조회합니다", tags = {"Chat"})
    public ResponseEntity<ApiResponse<ChatRoomResponse>> roomInfo(@PathVariable Long roomId) {
        return ResponseEntity.ok(ApiResponse.ok("채팅방 정보가 조회되었습니다.", chatRoomService.roomInfo(roomId)));
    }

    // 채팅방 접속시 닉네임 정보 조회
    @GetMapping("/chat/enter/me")
    @ResponseBody
    @Operation(summary = "본인 닉네임 조회", description = "본인의 닉네임을 조회합니다", tags = {"Chat"})
    public ResponseEntity<ApiResponse<NicknameResponse>> nicknameInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok("닉네임이 조회되었습니다", NicknameResponse.from(userDetails.getUsername())));
    }

    // 채팅방 입장 시도
    @GetMapping("/chat-room/enter/{chatRoomId}")
    public ResponseEntity<ApiResponse<Void>> PostChatRoomEnter(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                               @PathVariable Long chatRoomId) {
        Long userId = userDetails.getUserId();
        String result = chatRoomService.chatRoomEnterValid(userId, chatRoomId);
        return ResponseEntity.ok(ApiResponse.ok(result, null));
    }

}
