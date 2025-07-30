package org.example.pdnight.domain1.follow.repository;

import org.example.pdnight.domain1.follow.entity.Follow;
import org.example.pdnight.domain1.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow,Long> {
    Boolean existsByFollowerAndFollowing(User follower, User following);

    Optional<Follow> findByFollowerAndFollowing(User follower, User following);
}
