package org.example.pdnight.domain.chat.presentation.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.pdnight.domain.chat.domain.ChatMessage;
import org.example.pdnight.domain.chat.enums.MessageType;


@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageDto {

    private MessageType messageType;
    private String roomId;
    private String sender;
    private String message;

    private ChatMessageDto(ChatMessage chatMessage) {
        this.messageType = chatMessage.getMessageType();
        this.roomId = chatMessage.getRoomId();
        this.sender = chatMessage.getSender();
        this.message = chatMessage.getMessage();
    }

    public static ChatMessageDto from(ChatMessage chatMessage) {
        return new ChatMessageDto(chatMessage);
    }
}
