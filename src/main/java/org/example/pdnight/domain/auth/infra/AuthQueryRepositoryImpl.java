package org.example.pdnight.domain.auth.infra;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.auth.domain.AuthReader;
import org.example.pdnight.domain.auth.domain.entity.Auth;
import org.example.pdnight.domain.user.entity.QUser;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthQueryRepositoryImpl implements AuthReader {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Auth> findByEmail(String email) {
        QUser auth = QUser.user;
        // QAuth auth = QAuth.auth;

        return queryFactory
                .select(auth)
                .where(auth.email.eq(email))
                .stream()
                .findFirst();
    }
}
