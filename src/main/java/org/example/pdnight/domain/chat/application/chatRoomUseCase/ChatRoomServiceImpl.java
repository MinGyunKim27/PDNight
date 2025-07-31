package org.example.pdnight.domain.chat.application.chatRoomUseCase;



import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.chat.domain.ChatRoom;
import org.example.pdnight.domain.chat.presentation.dto.request.ChatMessageDto;
import org.example.pdnight.domain.chat.presentation.dto.response.ChatRoomResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService{

    private final ChatRoomReaderService chatRoomCommandService;
    private final ChatRoomCommanderService chatRoomQueryService;

    @Override
    public List<ChatRoomResponseDto> list() {
        return chatRoomCommandService.list();
    }

    @Override
    public ChatRoom create(String name) {
        return chatRoomQueryService.create(name);
    }

    @Override
    public ChatRoomResponseDto roomInfo(Long roomId) {
        return chatRoomCommandService.roomInfo(roomId);
    }

    @Override
    public String chatRoomEnter(Long userId, Long chatRoomId) {
        return chatRoomQueryService.chatRoomEnter(userId, chatRoomId);
    }

    @Override
    public void enterChatRoom(String roomId) {
        chatRoomQueryService.enterChatRoom(roomId);
    }

    @Override
    public void sendMessage(ChatMessageDto message) {
        chatRoomQueryService.sendMessage(message);
    }
}
