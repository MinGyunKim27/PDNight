package org.example.pdnight.domain.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "follower_id")
    private User follower;

    @ManyToOne
    @JoinColumn(name = "following_id")
    private User following;

    private Follow(User loginUser, User targerUser){
        this.follower = loginUser;
        this.following = targerUser;
    }

    public static Follow create(User loginUser, User targerUser){
        return new Follow(loginUser, targerUser);
    }
}
