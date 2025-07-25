package org.example.pdnight.domain.chatRoom.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "chat_rooms")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatRoomName;

    private Long postId;

    public ChatRoom(String name) {
        chatRoomName = name;
    }

    public ChatRoom(String name, Long postId) {
        chatRoomName = name;
        this.postId = postId;
    }

    public ChatRoom() {
    }
}
