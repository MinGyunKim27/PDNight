package org.example.pdnight.domain.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.global.common.entity.Timestamped;

@Entity
@Table(name = "hobbies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hobby extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hobby", unique = true)
    private String hobby;

    private Hobby(String hobby){
        this.hobby = hobby;
    }

    public static Hobby from(String hobby) {
        return new Hobby(hobby);
    }

}
