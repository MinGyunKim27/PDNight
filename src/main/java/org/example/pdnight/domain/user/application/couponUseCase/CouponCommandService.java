package org.example.pdnight.domain.user.application.couponUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.user.domain.couponDomain.CouponCommandQuery;
import org.example.pdnight.domain.user.domain.entity.*;
import org.example.pdnight.domain.user.infra.couponInfra.CouponJpaRepository;
import org.example.pdnight.domain.user.presentation.dto.couponDto.request.CouponRequest;
import org.example.pdnight.domain.user.presentation.dto.couponDto.request.UpdateCouponRequest;
import org.example.pdnight.domain.user.presentation.dto.couponDto.response.CouponUpdateResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponCommandService {

    private final CouponCommandQuery couponCommandQuery;

    // --------------------- Admin Api ------------------------------------------------//
    // 쿠폰생성
    @Transactional
    public CouponUpdateResponse createCoupon(CouponRequest dto) {
        Coupon coupon = Coupon.create(dto.getCouponInfo(), dto.getDeadlineAt());

        couponCommandQuery.save(coupon);
        return CouponUpdateResponse.from(coupon);
    }

    // 쿠폰 부여
    @Transactional
    public CouponUpdateResponse giveCouponToUser(CouponRequest dto) {
        // Coupon 정보 가져옴
        // UserCoupon 생성
        // user에게 이벤트로 UserCoupon 넘겨서, UserCoupon 리스트에 추가함
        // UserCoupon 리턴

        Coupon coupon = Coupon.create(dto.getCouponInfo(), dto.getDeadlineAt());

        couponCommandQuery.save(coupon);
        return CouponUpdateResponse.from(coupon);
    }


    // 쿠폰 수정
    @Transactional
    public CouponUpdateResponse updateCoupon(Long id, UpdateCouponRequest dto) {
        Coupon coupon = getCouponById(id);
        coupon.updateCoupon(dto.getCouponInfo(), dto.getDeadlineAt());
        return CouponUpdateResponse.from(coupon);
    }

    // 쿠폰삭제
    @Transactional
    public void deleteCoupon(Long id) {
        Coupon coupon = getCouponById(id);
        couponCommandQuery.delete(coupon);
    }

    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // get
    private Coupon getCouponById(Long couponId) {
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
