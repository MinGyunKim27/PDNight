package org.example.pdnight.domain.user.application.couponUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.auth.presentation.dto.request.SignupRequestDto;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.common.helper.GetHelper;
import org.example.pdnight.domain.user.domain.entity.*;
import org.example.pdnight.domain.user.infra.couponInfra.CouponJpaRepository;
import org.example.pdnight.domain.user.presentation.dto.couponDto.request.CouponRequestDto;
import org.example.pdnight.domain.user.presentation.dto.couponDto.request.UpdateCouponRequestDto;
import org.example.pdnight.domain.user.presentation.dto.couponDto.response.CouponResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponCommandService {

    private final CouponJpaRepository couponJpaRepository;
    private final GetHelper helper;

    // 쿠폰사용
    @Transactional
    public CouponResponseDto useCoupon(Long couponId, Long userId) {
        UserCoupon coupon = getMyCouponById(couponId, userId);

        validateUseCoupon(userId, coupon);

        coupon.use(); // 쿠폰 사용 처리
        return CouponResponseDto.from(coupon);
    }

    // --------------------- Admin Api ------------------------------------------------//
    // 쿠폰 수정
    @Transactional
    public CouponResponseDto updateCoupon(Long id, UpdateCouponRequestDto dto) {
        Coupon coupon = getCouponById(id);
        coupon.updateCoupon(dto.getCouponInfo(), dto.getDeadlineAt());
        return CouponResponseDto.from(coupon);
    }

    // 쿠폰삭제
    @Transactional
    public void deleteCoupon(Long id) {
        Coupon coupon = getCouponById(id);
        couponJpaRepository.delete(coupon);
    }

    // 쿠폰생성
    @Transactional
    public CouponResponseDto createCoupon(CouponRequestDto dto) {

        User user = helper.getUserByIdOrElseThrow(dto.getUserId());
        Coupon coupon = Coupon.create(user, dto.getCouponInfo(), dto.getDeadlineAt());

        couponJpaRepository.save(coupon);
        user.addCoupon(coupon.getId()); // 양방향 연관관계 리스트에 쿠폰 추가
        return CouponResponseDto.from(coupon);
    }

    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // get
    private UserCoupon getCouponById(Long couponId) {
        return couponJpaRepository.findById(couponId)
                .orElseThrow(() -> new BaseException(ErrorCode.COUPON_NOT_FOUND));
    }

    // get
    private UserCoupon getMyCouponById(Long couponId, Long userId){
        return couponJpaRepository.findByCouponIdAndUserId(couponId, userId)
                .orElseThrow(() -> new BaseException(ErrorCode.COUPON_NOT_FOUND));
    }

    // validate
    private void validateUseCoupon(Long userId, UserCoupon userCoupon) {
        if (!userCoupon.getUserId().equals(userId)) {
            throw new BaseException(ErrorCode.COUPON_FORBIDDEN); // 본인 쿠폰만 사용 가능
        }
    }

//    private List<Hobby> getHobbyList(SignupRequestDto request) {
//        List<Hobby> hobbyList = new ArrayList<>();
//        if (request.getHobbyIdList() != null && !request.getHobbyIdList().isEmpty()) {
//            hobbyList = hobbyRepositoryQuery.findByIdList(request.getHobbyIdList());
//        }
//        return hobbyList;
//    }
//
//    private List<TechStack> getTechStackList(SignupRequestDto request) {
//        List<TechStack> techStackList = new ArrayList<>();
//        if (request.getTechStackIdList() != null && !request.getTechStackIdList().isEmpty()) {
//            techStackList = techStackRepositoryQuery.findByIdList(request.getTechStackIdList());
//        }
//        return techStackList;
//    }
//
//    private Set<UserHobby> getUserHobbySet(List<Hobby> hobbyList, User user) {
//        return hobbyList.stream()
//                .map(hobby -> UserHobby.create(user, hobby))
//                .collect(Collectors.toSet());
//    }
//
//    private Set<UserTech> getUserTechSet(List<TechStack> techStackList, User user) {
//        return techStackList.stream()
//                .map(techStack -> UserTech.create(user, techStack))
//                .collect(Collectors.toSet());
//    }
}
