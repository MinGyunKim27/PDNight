package org.example.pdnight.domain.user.application.userUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.user.domain.entity.Coupon;
import org.example.pdnight.domain.user.domain.entity.Follow;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.entity.UserCoupon;
import org.example.pdnight.domain.user.domain.userDomain.UserCommandQuery;
import org.example.pdnight.domain.user.domain.userDomain.UserReader;
import org.example.pdnight.domain.user.presentation.dto.couponDto.response.CouponResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.UserNicknameUpdate;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.UserUpdateRequest;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.FollowResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserResponse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.example.pdnight.domain.common.enums.ErrorCode.*;

@Component
@RequiredArgsConstructor
public class UserCommandService {

    private final UserReader userReader;
    private final UserCommandQuery userCommandQuery;
    private final UserInfoAssembler userInfoAssembler;

    @Transactional
    public UserResponse updateMyProfile(Long userId, UserUpdateRequest request) {
        User user = getUserById(userId);

        // 수정 로직
        user.updateProfile(request.getName(),
                request.getNickname(),
                request.getGender(),
                request.getAge(),
                request.getJobCategory(),
                request.getRegion(),
                request.getComment(),
                request.getHobbyIdList(),
                request.getTechStackIdList());

        //todo: 여기서 hobby, tech 호출, 중간 테이블에 기록
        userCommandQuery.save(user);

        return userInfoAssembler.toDto(user);
    }

    public UserResponse updateNickname(Long userId, UserNicknameUpdate dto){
        User user = getUserById(userId);

        user.updateNickname(dto.getNickname());

        userCommandQuery.save(user);

        return userInfoAssembler.toDto(user);
    }

    public void delete(Long userId) {
        User user = getUserById(userId);

        if (user.getIsDeleted()) {
            throw new BaseException(ErrorCode.USER_DEACTIVATED);
        }

        user.softDelete();
    }

    //팔로우
    public FollowResponse follow(Long userId, Long loginId) {

        User follower = getUserById(loginId);
        User following = getUserById(userId);
        // 자기 자신 팔로우 방지
        follower.validateIsSelfFollow(following,INVALID_FOLLOW_SELF);

        // 중복 팔로우 방지
        follower.validateExistFollowing(following);

        Follow follow = Follow.create(follower, following);

        // 팔로우 추가
        follower.addFollow(following,follow);

        return FollowResponse.from(follow);
    }

    //언팔로우
    @Transactional
    public void unfollow(Long userId, Long loginId) {

        User follower = getUserById(loginId);
        User following = getUserById(userId);

        // 자기 자신 언팔 방지
        follower.validateIsSelfFollow(following, INVALID_UNFOLLOW_SELF);
        //팔로우 중이 아님
        follower.validateIsNotFollowing(following, NOT_FOLLOWING);

        follower.unfollow(following);
    }


    // 쿠폰사용
    @Transactional
    public CouponResponse useCoupon(Long couponId, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        CouponResponse userCoupon = userReader.findUserCoupon(userId, now);
//        Coupon coupon = getCouponById(couponId);
        validateUseCoupon(userId, userCoupon);

        userCoupon.use(); // 쿠폰 사용 처리
        return CouponResponse.from(userCoupon, coupon);
    }

    // --------------------------------------------------------------------------------------------------------//
    // --------------------------------------------------------------------------------------------------------//
    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // --------------------------------------------------------------------------------------------------------//
    // --------------------------------------------------------------------------------------------------------//

    // get
    private User getUserById(Long id) {
        return userReader.findById(id).orElseThrow(
                () -> new BaseException(ErrorCode.USER_NOT_FOUND));
    }

    // get

    // get

    // validate
    private void validateUseCoupon(Long userId, UserCoupon userCoupon) {
        if (!userCoupon.getUserId().equals(userId)) {
            throw new BaseException(ErrorCode.COUPON_FORBIDDEN); // 본인 쿠폰만 사용 가능
        }
    }
}
