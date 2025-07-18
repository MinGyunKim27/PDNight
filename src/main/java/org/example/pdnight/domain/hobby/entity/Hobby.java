package org.example.pdnight.domain.hobby.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.common.entity.Timestamped;

@Entity
@Table(name = "hobbies")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Hobby extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hobby;

    public Hobby(String hobby){
        this.hobby = hobby;
    }

}
