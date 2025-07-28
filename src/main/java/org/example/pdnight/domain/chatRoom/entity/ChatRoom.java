package org.example.pdnight.domain.chatRoom.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "chat_rooms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatRoomName;

    private Long postId;

    private ChatRoom(String name) {
        chatRoomName = name;
    }

    private ChatRoom(String name, Long postId) {
        chatRoomName = name;
        this.postId = postId;
    }

    public static ChatRoom create(String name) {
        return new ChatRoom(name);
    }

    public static ChatRoom createFromPost(String name, Long postId) {
        return new ChatRoom(name, postId);
    }
}
