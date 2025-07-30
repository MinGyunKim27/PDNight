package org.example.pdnight.domain1.participant.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain1.common.entity.Timestamped;
import org.example.pdnight.domain1.common.enums.JoinStatus;
import org.example.pdnight.domain1.post.entity.Post;
import org.example.pdnight.domain1.user.entity.User;

@Entity
@Table(name = "posts_participants")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostParticipant extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private JoinStatus status;

    private PostParticipant(Post post, User user, JoinStatus status) {
        this.post = post;
        this.user = user;
        this.status = status;
    }

    public static PostParticipant create(Post post, User user) {
        return new PostParticipant(post, user, JoinStatus.PENDING);
    }

    public static PostParticipant createIsFirst(Post post, User user) {
        return new PostParticipant(post, user, JoinStatus.ACCEPTED);
    }

    public void changeStatus(JoinStatus status) {
        this.status = status;
    }
}
