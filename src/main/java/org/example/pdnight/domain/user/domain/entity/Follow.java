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

    /**
     * 팔로우 하는 사람
     */
    @ManyToOne
    @JoinColumn(name = "follower_id")
    private User follower;

    /**
     * 팔로우 받는 사람
     */
    @ManyToOne
    @JoinColumn(name = "following_id")
    private User following;

    private Follow(User loginUser, User targetUser){
        this.follower = loginUser;
        this.following = targetUser;
    }

    public static Follow create(User loginUser, User targetUser){
        return new Follow(loginUser, targetUser);
    }
}
