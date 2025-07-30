package org.example.pdnight.domain1.user.repository;

import org.example.pdnight.domain1.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryQuery {
    Page<User> searchUsers(String search, Pageable pageable);
}
