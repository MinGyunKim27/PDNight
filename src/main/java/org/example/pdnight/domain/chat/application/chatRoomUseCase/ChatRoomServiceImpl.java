package org.example.pdnight.domain.chat.application.chatRoomUseCase;



import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.chat.domain.ChatRoom;
import org.example.pdnight.domain.chat.presentation.dto.response.ChatRoomResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService{

    private final ChatRoomReaderService chatRoomReaderService;
    private final ChatRoomCommanderService chatRoomCommanderService;

    @Override
    public List<ChatRoomResponseDto> list() {
        return chatRoomReaderService.list();
    }

    @Override
    public ChatRoom create(String name) {
        return chatRoomCommanderService.create(name);
    }

    @Override
    public ChatRoomResponseDto roomInfo(Long roomId) {
        return chatRoomReaderService.roomInfo(roomId);
    }

    @Override
    public String chatRoomEnterValid(Long userId, Long chatRoomId) {
        return chatRoomCommanderService.chatRoomEnterValid(userId, chatRoomId);
    }

    @Override
    public void createFromPost(Long id) {
        chatRoomCommanderService.createFromPost(id);
    }

}
