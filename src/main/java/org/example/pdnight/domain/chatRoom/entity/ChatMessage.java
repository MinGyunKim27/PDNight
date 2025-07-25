package org.example.pdnight.domain.chatRoom.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.pdnight.domain.chatRoom.dto.ChatMessageDto;
import org.example.pdnight.domain.chatRoom.enums.MessageType;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "chat_messages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomId;

    private String sender;

    private String message;

    private MessageType messageType;

    private LocalDateTime sentAt;

    public ChatMessage(ChatMessageDto roomMessage) {
        this.roomId = roomMessage.getRoomId();
        this.sender = roomMessage.getSender();
        this.message = roomMessage.getMessage();
        this.messageType = roomMessage.getMessageType();
        sentAt = LocalDateTime.now();
    }

    public ChatMessage() {

    }
}
