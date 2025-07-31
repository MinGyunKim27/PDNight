package org.example.pdnight.domain.user.infra.userInfra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.userDomain.UserCommander;
import org.example.pdnight.domain.user.domain.entity.User;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserCommanderImpl implements UserCommander {
    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(final User user) {
        return userJpaRepository.save(user);
    }
}
