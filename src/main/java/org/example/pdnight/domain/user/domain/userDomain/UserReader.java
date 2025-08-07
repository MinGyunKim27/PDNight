package org.example.pdnight.domain.user.domain.userDomain;

import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.entity.UserCoupon;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserCouponResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.FollowingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserReader {
    Optional<User> findById(Long id);

    Optional<User> findByIdWithFollow(Long id);

    Page<User> searchUsers(String search, Pageable pageable);

    Page<User> findAll(Pageable pageable);

    Page<FollowingResponse> findFollowingsByUserId(Long userId, Pageable pageable);

    Page<UserCouponResponse> findUserCoupons(Long userId, LocalDateTime now, Pageable pageable);

    Optional<UserCoupon> findUserCoupon(Long userId, Long couponId, LocalDateTime now);

    List<UserCoupon> findByDeadlineAtBetween(LocalDateTime start, LocalDateTime end);
}
