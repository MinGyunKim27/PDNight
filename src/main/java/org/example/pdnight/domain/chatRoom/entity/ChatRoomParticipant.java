package org.example.pdnight.domain.chatRoom.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class ChatRoomParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_rooms_id")
    private ChatRoom chatRoom;

    private Long userId;

    public ChatRoomParticipant(ChatRoom chatRoom, Long id) {
        this.chatRoom = chatRoom;
        this.userId =id;
    }

    public ChatRoomParticipant() {

    }
}
