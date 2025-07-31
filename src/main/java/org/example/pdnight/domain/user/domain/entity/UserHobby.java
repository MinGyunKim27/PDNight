package org.example.pdnight.domain.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_hobby")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserHobby {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Long hobbyId;

    private UserHobby(User user, Long hobbyId) {
        this.user = user;
        this.hobbyId = hobbyId;
    }

    public static UserHobby create(User user, Long hobbyId) {
        return new UserHobby(user, hobbyId);
    }
}
