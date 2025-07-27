package org.example.pdnight.domain.chatRoom.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomViewController {
    // 채팅 리스트 화면
    @GetMapping("/view")
    public String rooms() {
        return "chat/room";
    }

    // 채팅방 입장 화면
    @GetMapping("/view/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "chat/roomdetail";
    }
}