package org.example.pdnight.domain.hobby.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.post.entity.Post;

@Entity
@Table(name = "post_hobby")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostHobby {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hobby_id")
    private Hobby hobby;

    private PostHobby(Post post, Hobby hobby) {
        this.post = post;
        this.hobby = hobby;
    }

    public static PostHobby create(Post post, Hobby hobby) {
        return new PostHobby(post, hobby);
    }
}
