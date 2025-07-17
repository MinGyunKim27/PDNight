package org.example.pdnight.domain.hobby.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private Long id;

    @Column(length = 255)
    private String hobby;
}
