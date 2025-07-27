package org.example.pdnight.domain.chatRoom.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.pdnight.domain.chatRoom.entity.ChatMessage;
import org.example.pdnight.domain.chatRoom.enums.MessageType;

@Getter
@Setter
public class ChatMessageDto {

    private MessageType messageType;
    private String roomId;
    private String sender;
    private String message;

    public ChatMessageDto(ChatMessage chatMessage) {
        this.messageType = chatMessage.getMessageType();
        this.roomId = chatMessage.getRoomId();
        this.sender = chatMessage.getSender();
        this.message = chatMessage.getMessage();
    }

    public ChatMessageDto() {
    }

    public static ChatMessageDto from(ChatMessage chatMessage) {
        return new ChatMessageDto(chatMessage);
    }
}
