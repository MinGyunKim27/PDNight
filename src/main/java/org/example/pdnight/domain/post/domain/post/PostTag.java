package org.example.pdnight.domain.post.domain.post;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private Long tagId;

    public PostTag(Post post, Long tagId) {
        this.post = post;
        this.tagId = tagId;
    }

    public static PostTag create(Post post, Long tagId) {
        return new PostTag(post, tagId);
    }
}