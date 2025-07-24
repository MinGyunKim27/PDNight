package org.example.pdnight.domain.techStack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.post.entity.Post;

@Entity
@Table(name = "post_tech")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostTech {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teck_stack_id")
    private TechStack techStack;

    public PostTech(Post post, TechStack techStack) {
        this.post = post;
        this.techStack = techStack;
    }
}
