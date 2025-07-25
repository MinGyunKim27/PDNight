package org.example.pdnight.domain.chatRoom.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.pdnight.domain.chatRoom.entity.Chatting;
import org.example.pdnight.domain.chatRoom.enums.MessageType;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageDto {

    private MessageType messageType;
    private String roomId;
    private String sender;
    private String message;

    public ChatMessageDto(Chatting chatting) {
        this.messageType = chatting.getMessageType();
        this.roomId = chatting.getRoomId();
        this.sender = chatting.getSender();
        this.message = chatting.getMessage();
    }
}
