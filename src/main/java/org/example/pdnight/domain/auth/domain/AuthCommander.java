package org.example.pdnight.domain.auth.domain;

import org.example.pdnight.domain.auth.domain.entity.Auth;

import java.util.Optional;

public interface AuthCommander {

    Auth save(Auth auth);

    Optional<Auth> findById(Long authId);

    Optional<Auth> findByEmail(String email);
}
