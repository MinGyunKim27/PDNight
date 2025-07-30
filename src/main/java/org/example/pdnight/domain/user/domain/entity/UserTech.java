package org.example.pdnight.domain.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_tech")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserTech {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teck_stack_id")
    private TechStack techStack;

    private UserTech(User user, TechStack techStack) {
        this.user = user;
        this.techStack = techStack;
    }

    public static UserTech create(User user, TechStack techStack) {
        return new UserTech(user, techStack);
    }
}
