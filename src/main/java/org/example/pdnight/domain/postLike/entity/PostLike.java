package org.example.pdnight.domain.postLike.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.common.entity.Timestamped;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.user.entity.User;

@Entity
@Table(
        name = "post_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private PostLike(Post post, User user) {
        this.post = post;
        this.user = user;
    }

    public static PostLike create(Post post, User user) {
        return new PostLike(post, user);
    }
}
