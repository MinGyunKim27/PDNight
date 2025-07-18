package org.example.pdnight.domain.user.repository;

import org.example.pdnight.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
