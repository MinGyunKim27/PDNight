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

    private Long techStackId;

    private UserTech(User user, Long techStackId) {
        this.user = user;
        this.techStackId = techStackId;
    }

    public static UserTech create(User user, Long techStackId) {
        return new UserTech(user, techStackId);
    }
}
