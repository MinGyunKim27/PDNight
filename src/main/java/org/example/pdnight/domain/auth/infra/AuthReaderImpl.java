package org.example.pdnight.domain.auth.infra;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.auth.domain.AuthReader;
import org.example.pdnight.domain.auth.domain.entity.Auth;
import org.example.pdnight.domain.auth.domain.entity.QAuth;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthReaderImpl implements AuthReader {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Auth> findByEmail(String email) {
        QAuth auth = QAuth.auth;

        return queryFactory
                .selectFrom(auth)
                .where(auth.email.eq(email))
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Auth> findById(Long id) {
        QAuth auth = QAuth.auth;

        return queryFactory
                .selectFrom(auth)
                .where(auth.id.eq(id))
                .stream()
                .findFirst();
    }
}
