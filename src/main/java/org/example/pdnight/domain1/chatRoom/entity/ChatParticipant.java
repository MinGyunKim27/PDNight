package org.example.pdnight.domain1.chatRoom.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "chat_participants")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public static ChatParticipant from(ChatRoom chatRoom, Long id) {
        return new ChatParticipant(chatRoom, id);
    }
}
