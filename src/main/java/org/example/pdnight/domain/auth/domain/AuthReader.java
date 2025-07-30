package org.example.pdnight.domain.auth.domain;

import org.example.pdnight.domain.auth.domain.entity.Auth;
import org.example.pdnight.domain.user.domain.entity.User;

import java.util.Optional;

public interface AuthReader {

    Optional<Auth> findByEmail(String email);

}
