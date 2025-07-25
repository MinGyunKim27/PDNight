package org.example.pdnight.domain.chatRoom.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.chatRoom.service.ChattingService;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ChattingRoomController {

    private final ChattingService chattingService;

    @GetMapping("posts/chatRoom/enter/{chatRoomId}")
    public ResponseEntity<ApiResponse<Void>> PostChatRoomEnter(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @PathVariable Long chatRoomId){
        Long userId = userDetails.getUserId();
        String result = chattingService.PostChatRoomEnter(userId, chatRoomId);
        return ResponseEntity.ok(ApiResponse.ok(result, null));
    }
}
