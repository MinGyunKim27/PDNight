package org.example.pdnight.domain.user.infra.userInfra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.userDomain.UserCommandQuery;
import org.example.pdnight.domain.user.domain.entity.User;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserCommandQuery {
    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(final User user) {
        return userJpaRepository.save(user);
    }
}
