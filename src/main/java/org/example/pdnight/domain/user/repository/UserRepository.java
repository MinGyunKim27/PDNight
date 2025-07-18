package org.example.pdnight.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import org.example.pdnight.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
