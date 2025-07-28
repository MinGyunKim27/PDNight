package org.example.pdnight.domain.hobby.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.user.entity.User;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hobby_id")
    private Hobby hobby;

    private UserHobby(User user, Hobby hobby) {
        this.user = user;
        this.hobby = hobby;
    }

    public static UserHobby create(User user, Hobby hobby) {
        return new UserHobby(user, hobby);
    }
}
