package org.example.pdnight.domain.chatRoom.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "chat_participants")
public class ChatParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_rooms_id")
    private ChatRoom chatRoom;

    private Long userId;

    private ChatParticipant(ChatRoom chatRoom, Long id) {
        this.chatRoom = chatRoom;
        this.userId = id;
    }

    protected ChatParticipant() {
    }

    public static ChatParticipant from(ChatRoom chatRoom, Long id) {
        return new ChatParticipant(chatRoom, id);
    }
}
