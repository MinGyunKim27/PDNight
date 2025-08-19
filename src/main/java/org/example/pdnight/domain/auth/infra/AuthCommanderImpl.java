package org.example.pdnight.domain.auth.infra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.auth.domain.AuthCommander;
import org.example.pdnight.domain.auth.domain.entity.Auth;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class AuthCommanderImpl implements AuthCommander {

    private final AuthJpaRepository authJpaRepository;

    @Override
    public Auth save(Auth auth) {
        return authJpaRepository.save(auth);
    }

    @Override
    public Optional<Auth> findById(Long authId) {
        return authJpaRepository.findById(authId);
    }

    @Override
    public Optional<Auth> findByEmail(String email) {
        return authJpaRepository.findByEmail(email);
    }
    
}