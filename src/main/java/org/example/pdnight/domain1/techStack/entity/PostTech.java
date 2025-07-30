package org.example.pdnight.domain1.techStack.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain1.post.entity.Post;

@Entity
@Table(name = "post_tech")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostTech {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teck_stack_id")
    private TechStack techStack;

    private PostTech(Post post, TechStack techStack) {
        this.post = post;
        this.techStack = techStack;
    }

    public static PostTech from(Post post, TechStack techStack) {
        return new PostTech(post, techStack);
    }
}
