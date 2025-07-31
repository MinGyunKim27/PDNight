package org.example.pdnight.domain.chat.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatParticipant> participants;

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

    public void addParticipants(ChatParticipant authorId) {
        participants.add(authorId);
    }
}
