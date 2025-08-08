package org.example.pdnight.domain.auth.infra;

import org.example.pdnight.domain.auth.domain.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthJpaRepository extends JpaRepository<Auth, Long> {
    Optional<Auth> findByEmail(String email);
}
