package org.example.pdnight.domain.user.application.userUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.GiveCouponRequest;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.UserNicknameUpdate;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.UserUpdateRequest;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.*;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserCommanderService userCommanderService;
    private final UserReaderService userReaderService;

    // ---------------------- Command Api -----------------------------------------------------//

    public UserResponse updateMyProfile(Long userId, UserUpdateRequest request) {
        return userCommanderService.updateMyProfile(userId, request);
    }

    public FollowResponse follow(Long userId, Long loginId) {
        return userCommanderService.follow(userId, loginId);
    }


    public void unfollow(Long userId, Long loginId) {
        userCommanderService.unfollow(userId, loginId);
    }

    public void delete(Long userId) {
        userCommanderService.delete(userId);
    }

    public UserCouponResponse useCoupon(Long couponId, Long userId) {
        return userCommanderService.useCoupon(couponId, userId);
    }
    // --------------------- Admin Command Api ------------------------------------------------//

    public UserCouponResponse giveCouponToUser(GiveCouponRequest request) {
        return userCommanderService.giveCouponToUser(request);
    }

    public UserResponse updateNickname(Long userId, UserNicknameUpdate dto) {
        return userCommanderService.updateNickname(userId, dto);
    }

    // ----------------------------------------------------------------------------------------//
    // ---------------------- 조회 Api ---------------------------------------------------------//

    @Override
    public UserResponse getMyProfile(Long userId) {
        return userReaderService.getProfile(userId);
    }

    @Override
    public UserResponse getProfile(Long userId) {
        return userReaderService.getProfile(userId);
    }

    @Override
    public UserEvaluationResponse getEvaluation(Long userId) {
        return userReaderService.getEvaluation(userId);
    }

    //유저 이름이나 닉네임이나 이메일로 검색
    @Override
    public PagedResponse<UserResponse> searchUsers(String search, Pageable pageable) {
        return userReaderService.searchUsers(search, pageable);
    }

    @Override
    public Page<FollowingResponse> getFollowings(Long myId, Pageable pageable) {
        return userReaderService.getFollowings(myId, pageable);
    }

    // 보유한 사용가능한 쿠폰 조회
    @Override
    public PagedResponse<UserCouponResponse> getValidCoupons(Long userId, Pageable pageable) {
        return userReaderService.getValidCoupons(userId, pageable);
    }
    // --------------------- Admin 조회 Api ----------------------------------------------------//

    @Override
    public PagedResponse<UserResponse> getAllUsers(Pageable pageable) {
        return userReaderService.getAllUsers(pageable);
    }

}
