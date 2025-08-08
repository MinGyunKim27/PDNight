package org.example.pdnight.domain.user.infra.hobbyInfra;

import org.example.pdnight.domain.user.domain.entity.Hobby;
import org.springframework.data.jpa.repository.JpaRepository;


public interface HobbyJpaRepository extends JpaRepository<Hobby,Long> {
    boolean existsByHobby(String hobby);
}
