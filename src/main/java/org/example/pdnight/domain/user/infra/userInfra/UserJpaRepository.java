package org.example.pdnight.domain.user.infra.userInfra;

import org.example.pdnight.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
}
