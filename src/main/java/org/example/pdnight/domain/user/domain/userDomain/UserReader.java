package org.example.pdnight.domain.user.domain.userDomain;

import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.entity.UserCoupon;
import org.example.pdnight.domain.user.presentation.dto.couponDto.response.CouponResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.FollowingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserReader {
    Optional<User> findById(Long id);

    Page<User> searchUsers(String search, Pageable pageable);

    //포스트 도메인에 사용할 임시 메서드 유저도메인에 해당 메서드 추가시 삭제
    Optional<User> findByIdAndIsDeletedFalse(Long id);

    Page<User> findAll(Pageable pageable);

    Page<FollowingResponse> findFollowingsByUserId(Long userId, Pageable pageable);

    Page<CouponResponse> findUserCoupons(Long userId, LocalDateTime now, Pageable pageable);

    CouponResponse findUserCoupon(Long userId, LocalDateTime now);
}
