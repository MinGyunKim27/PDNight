package org.example.pdnight.domain.follow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.user.entity.User;

@Entity
@Getter
//동일한 유저가 같은 사람 중복팔로우하는 경우 방지
@Table(name = "follows", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"follower_id", "following_id"})
})
@NoArgsConstructor
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

    private Follow(User follower,User following){
        this.follower = follower;
        this.following = following;
    }

    public static Follow create(User follower,User following){
        return new Follow(follower,following);
    }
}
