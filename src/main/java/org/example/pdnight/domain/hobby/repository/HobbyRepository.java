package org.example.pdnight.domain.hobby.repository;

import jakarta.validation.constraints.NotNull;
import org.example.pdnight.domain.hobby.entity.Hobby;
import org.springframework.data.jpa.repository.JpaRepository;


public interface HobbyRepository extends JpaRepository<Hobby,Long> {
    Boolean existsHobbiesByHobby(@NotNull String hobby);

    Hobby findByhobby(String hobby);

}
