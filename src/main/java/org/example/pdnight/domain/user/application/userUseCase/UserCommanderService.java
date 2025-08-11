package org.example.pdnight.domain.user.application.userUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.entity.Follow;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.entity.UserCoupon;
import org.example.pdnight.domain.user.domain.userDomain.UserCommander;
import org.example.pdnight.domain.user.domain.userDomain.UserProducer;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.GiveCouponRequest;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.UserNicknameUpdate;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.UserUpdateRequest;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.CouponInfo;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.FollowResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserCouponResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserResponse;
import org.example.pdnight.global.aop.SaveLog;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.enums.KafkaTopic;
import org.example.pdnight.global.common.exception.BaseException;
import org.example.pdnight.global.event.CouponIssuedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.example.pdnight.global.common.enums.ErrorCode.*;

@Component
@RequiredArgsConstructor
public class UserCommanderService {

    private final UserCommander userCommander;
    private final UserInfoAssembler userInfoAssembler;
    private final UserCouponPort userCouponPort;
    private final UserProducer producer;

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

        userCommander.save(user);

        return userInfoAssembler.toDto(user);
    }

    @Transactional
    @SaveLog
    public UserResponse updateNickname(Long userId, UserNicknameUpdate dto) {
        User user = getUserById(userId);

        user.updateNickname(dto.getNickname());

        userCommander.save(user);

        return userInfoAssembler.toDto(user);
    }

    @Transactional
    @SaveLog
    public void delete(Long userId) {
        User user = getUserById(userId);

        if (user.getIsDeleted()) {
            throw new BaseException(ErrorCode.USER_DEACTIVATED);
        }

        user.softDelete();
    }

    //팔로우
    @Transactional
    public FollowResponse follow(Long userId, Long loginId) {

        User targetUser = getUserById(userId);
        User loginUser = getUserById(loginId);

        // 자기 자신 팔로우 방지
        loginUser.validateIsSelfFollow(targetUser, INVALID_FOLLOW_SELF);

        // 중복 팔로우 방지
        loginUser.validateExistFollowing(targetUser);

        //follower to fromUser, following to toUser
        Follow follow = Follow.create(loginUser, targetUser);

        // 팔로우 추가
        loginUser.addFollow(targetUser, follow);
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

    // 쿠폰 부여
    @Transactional
    @SaveLog
    public UserCouponResponse giveCouponToUser(GiveCouponRequest request) {
        User user = getUserById(request.getUserId());

        CouponInfo couponInfo = userCouponPort.getCouponInfoById(request.getCouponId());

        UserCoupon userCoupon = UserCoupon.create(user, request.getCouponId(), couponInfo.getDefaultDeadlineDays());
        user.addCoupon(userCoupon);

        producer.produce(KafkaTopic.COUPON_ISSUED.topicName(), new CouponIssuedEvent(user.getId()));
        return UserCouponResponse.from(userCoupon);
    }

    // 쿠폰사용
    @Transactional
    public UserCouponResponse useCoupon(Long couponId, Long userId) {
        LocalDateTime now = LocalDateTime.now();

        // 유저가 가지는 쿠폰 하나 가져옴
        User user = userCommander.findByIdWithUserCoupon(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        UserCoupon userCoupon = user.getUserCoupons().stream()
                .filter(coupon -> coupon.getId().equals(couponId))
                .findFirst()
                .orElse(null);

        if (userCoupon == null) {
            throw new BaseException(COUPON_NOT_FOUND);
        }

        if (userCoupon.getDeadlineAt().isBefore(now)) {
            throw new BaseException(COUPON_EXPIRED);
        }

        // 쿠폰 사용 처리
        userCoupon.use();

        return UserCouponResponse.from(userCoupon);
    }

    // --------------------------------------------------------------------------------------------------------//
    // --------------------------------------------------------------------------------------------------------//
    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // --------------------------------------------------------------------------------------------------------//
    // --------------------------------------------------------------------------------------------------------//

    // get
    private User getUserById(Long id) {
        return userCommander.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
    }

}
