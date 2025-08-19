package org.example.pdnight.domain.chat.presentation.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.pdnight.domain.chat.enums.MessageType;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    private MessageType messageType;
    private String roomId;
    private String sender;
    @Setter
    private String message;

    private ChatMessage(org.example.pdnight.domain.chat.domain.ChatMessage chatMessage) {
        this.messageType = chatMessage.getMessageType();
        this.roomId = chatMessage.getRoomId();
        this.sender = chatMessage.getSender();
        this.message = chatMessage.getMessage();
    }

    public static ChatMessage from(org.example.pdnight.domain.chat.domain.ChatMessage chatMessage) {
        return new ChatMessage(chatMessage);
    }
}
