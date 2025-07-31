package org.example.pdnight.domain.user.application.userUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.user.presentation.dto.couponDto.response.CouponResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.UserNicknameUpdate;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.UserUpdateRequest;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.FollowResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.FollowingResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserEvaluationResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ---------------------- Command Api -----------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//

    public UserResponse updateMyProfile(Long userId, UserUpdateRequest request){
        return userCommandService.updateMyProfile(userId,request);
    }

    public FollowResponse follow(Long userId, Long loginId){
        return userCommandService.follow(userId,loginId);
    }


    public void unfollow(Long userId, Long loginId){
        userCommandService.unfollow(userId,loginId);
    }

    public void delete(Long userId){
        userCommandService.delete(userId);
    }

    public CouponResponse useCoupon(Long couponId, Long userId) {
        return userCommandService.useCoupon(couponId, userId);
    }
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // --------------------- Admin Command Api ------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//

    public UserResponse updateNickname(Long userId, UserNicknameUpdate dto){
        return userCommandService.updateNickname(userId,dto);
    }

    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ---------------------- 조회 Api ---------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//

    public UserResponse getMyProfile(Long userId){
        return userQueryService.getProfile(userId);
    }

    public UserResponse getProfile(Long userId) {
        return userQueryService.getProfile(userId);
    }

    public UserEvaluationResponse getEvaluation(Long userId) {
        return userQueryService.getEvaluation(userId);
    }

    //유저 이름이나 닉네임이나 이메일로 검색
    public PagedResponse<UserResponse> searchUsers(String search, Pageable pageable){
        return userQueryService.searchUsers(search,pageable);
    }

    @Override
    public Page<FollowingResponse> getFollowings(Long myId, Pageable pageable) {
        return userQueryService.getFollowings(myId,pageable);
    }

    // 보유한 사용가능한 쿠폰 조회
    @Override
    public PagedResponse<CouponResponse> getValidCoupons(Long userId, Pageable pageable) {
        return userQueryService.getValidCoupons(userId, pageable);
    }
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // --------------------- Admin 조회 Api ----------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//

    public PagedResponse<UserResponse> getAllUsers(Pageable pageable){
        return userQueryService.getAllUsers(pageable);
    }

}
