package org.example.pdnight.domain.chat.application.chatRoomUseCase;


import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.chat.domain.ChatParticipant;
import org.example.pdnight.domain.chat.domain.ChatRoom;
import org.example.pdnight.domain.chat.domain.ChatRoomCommander;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ChatRoomCommanderService {
    private final ChatRoomCommander chatRoomCommander;

    // 채팅방 생성
    public ChatRoom create(String name) {
        ChatRoom chatRoom = ChatRoom.create(name);
        return chatRoomCommander.save(chatRoom);
    }

    // 게시글 채팅방 참여시 채팅방 참여자인지 확인
    public String chatRoomEnterValid(Long userId, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomCommander.findById(chatRoomId).orElseThrow(() -> new BaseException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        if (chatRoom.getPostId() != null) {
            List<ChatParticipant> participants = chatRoom.getParticipants();
            if (participants.stream().noneMatch(participant -> participant.getUserId().equals(userId))) {
                throw new BaseException(ErrorCode.CHAT_ROOM_NOT_PARTICIPANT);
            }
            return "게시글 채팅방에 참여 되었습니다.";
        }
        return "오픈 채팅방에 참여 되었습니다.";
    }

}
