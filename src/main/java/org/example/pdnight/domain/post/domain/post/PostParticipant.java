package org.example.pdnight.domain.post.domain.post;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.domain1.common.entity.Timestamped;


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

    private Long userId;

    @Enumerated(EnumType.STRING)
    private JoinStatus status;

    private PostParticipant(Post post, Long userId, JoinStatus status) {
        this.post = post;
        this.userId = userId;
        this.status = status;
    }

    public static PostParticipant create(Post post, Long userId) {
        return new PostParticipant(post, userId, JoinStatus.PENDING);
    }

    public static PostParticipant createIsFirst(Post post, Long userId) {
        return new PostParticipant(post, userId, JoinStatus.ACCEPTED);
    }

    public void changeStatus(JoinStatus status) {
        this.status = status;
    }
}
