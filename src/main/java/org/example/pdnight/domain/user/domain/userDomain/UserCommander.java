package org.example.pdnight.domain.user.domain.userDomain;

import org.example.pdnight.domain.user.domain.entity.User;

import java.util.Optional;

public interface UserCommander {

    User save(User user);

    Optional<User> findById(Long userId);

    Optional<User> findByIdWithUserCoupon(Long userId);
}
