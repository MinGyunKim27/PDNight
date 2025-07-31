package org.example.pdnight.domain.user.application.userUseCase;

import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.GiveCouponRequest;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.UserNicknameUpdate;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.UserUpdateRequest;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    // ---------------------- Command Api -----------------------------------------------------//

    UserResponse updateMyProfile(Long userId, UserUpdateRequest request);

    FollowResponse follow(Long userId, Long loginId);


    void unfollow(Long userId, Long loginId);

    void delete(Long userId);

    UserCouponResponse giveCouponToUser(GiveCouponRequest request);

    UserCouponResponse useCoupon(Long couponId, Long userId);

    // --------------------- Admin Command Api ------------------------------------------------//

    UserResponse updateNickname(Long userId, UserNicknameUpdate dto);

    // ----------------------------------------------------------------------------------------//
    // ---------------------- 조회 Api ---------------------------------------------------------//

    UserResponse getMyProfile(Long userId);

    UserResponse getProfile(Long userId);

    UserEvaluationResponse getEvaluation(Long userId);

    //유저 이름이나 닉네임이나 이메일로 검색
    PagedResponse<UserResponse> searchUsers(String search, Pageable pageable);

    Page<FollowingResponse> getFollowings(Long myId, Pageable pageable);

    PagedResponse<UserCouponResponse> getValidCoupons(Long userId, Pageable pageable);

    // --------------------- Admin 조회 Api ----------------------------------------------------//

    PagedResponse<UserResponse> getAllUsers(Pageable pageable);
}
