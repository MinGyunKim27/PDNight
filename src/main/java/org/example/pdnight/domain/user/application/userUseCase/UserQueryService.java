package org.example.pdnight.domain.user.application.userUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.user.domain.entity.UserHobby;
import org.example.pdnight.domain.user.domain.entity.UserTech;
import org.example.pdnight.domain.user.domain.hobbyDomain.HobbyReader;
import org.example.pdnight.domain.user.domain.teckStackDomain.TechStackReader;
import org.example.pdnight.domain.user.domain.userDomain.UserReader;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.presentation.dto.couponDto.response.CouponResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.FollowingResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserEvaluationResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserQueryService {

    private final UserReader userReader;
    private final HobbyReader hobbyReader;
    private final TechStackReader techStackReader;
    private final UserInfoAssembler userInfoAssembler;

    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ---------------------- 조회 Api ---------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//

    public UserResponse getProfile(Long userId) {
        // id로 유저 조회
        User user = getUserById(userId);

        // UserResponseDto로 변환하여 반환
        return userInfoAssembler.toDto(user);
    }

    public UserEvaluationResponse getEvaluation(Long userId) {
        // id로 유저 조회
        User user = getUserById(userId);

        return UserEvaluationResponse.from(user);
    }

    //유저 이름이나 닉네임이나 이메일로 검색
    public PagedResponse<UserResponse> searchUsers(String search, Pageable pageable) {
        Page<User> page = userReader.searchUsers(search, pageable);

        List<UserResponse> responseList = userInfoAssembler.toDtoList(page);

        Page<UserResponse> dtos = new PageImpl<>(responseList, pageable, page.getTotalElements());

        return PagedResponse.from(dtos);
    }

    public Page<FollowingResponse> getFollowings(Long myId, Pageable pageable) {
        return userReader.findFollowingsByUserId(myId, pageable);
    }

    //  보유한 사용가능한 쿠폰 조회
    @Transactional(readOnly = true)
    public PagedResponse<CouponResponse> getValidCoupons(Long userId, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        return PagedResponse.from(userReader.findUserCoupon(userId, now, pageable));
    }

    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // --------------------- Admin 조회 Api ----------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//

    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> getAllUsers(Pageable pageable) {

        Page<User> page = userReader.findAll(pageable);

        List<UserResponse> responseList =  userInfoAssembler.toDtoList(page);

        Page<UserResponse> dtos = new PageImpl<>(responseList, pageable, page.getTotalElements());

        return PagedResponse.from(dtos);
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

}
