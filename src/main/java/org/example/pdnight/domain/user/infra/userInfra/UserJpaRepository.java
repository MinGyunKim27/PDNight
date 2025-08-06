package org.example.pdnight.domain.user.infra.userInfra;

import org.example.pdnight.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {


    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userCoupons WHERE u.id = :userId")
    Optional<User> findByIdWithUserCoupon(Long userId);
}
