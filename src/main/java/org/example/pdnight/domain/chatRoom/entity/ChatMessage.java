package org.example.pdnight.domain.chatRoom.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.chatRoom.enums.MessageType;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "chat_messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomId;

    private String sender;

    private String message;

    private MessageType messageType;

    private LocalDateTime sentAt;

    private ChatMessage(String roomId, String sender, String message, MessageType messageType) {
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
        this.messageType = messageType;
        sentAt = LocalDateTime.now();
    }

    public static ChatMessage from(String roomId, String sender, String message, MessageType messageType) {
        return new ChatMessage(roomId, sender, message, messageType);
    }
}
