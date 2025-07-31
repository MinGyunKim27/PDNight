package org.example.pdnight.domain.user.application.userUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.userDomain.UserReader;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.FollowingResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserCouponResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserEvaluationResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserReaderService {

    private final UserInfoAssembler userInfoAssembler;
    private final UserReader userReader;
    // private final HobbyReader hobbyReader;
    // private final TechStackReader techStackReader;

    @Transactional(readOnly = true)
    public UserResponse getProfile(Long userId) {
        // id로 유저 조회
        User user = getUserById(userId);

        // UserResponseDto로 변환하여 반환
        return userInfoAssembler.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserEvaluationResponse getEvaluation(Long userId) {
        // id로 유저 조회
        User user = getUserById(userId);

        return UserEvaluationResponse.from(user);
    }

    //유저 이름이나 닉네임이나 이메일로 검색
    @Transactional(readOnly = true)
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
    public PagedResponse<UserCouponResponse> getValidCoupons(Long userId, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        return PagedResponse.from(userReader.findUserCoupons(userId, now, pageable));
    }

    // --------------------- Admin 조회 Api ----------------------------------------------------//

    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> getAllUsers(Pageable pageable) {

        Page<User> page = userReader.findAll(pageable);

        List<UserResponse> responseList = userInfoAssembler.toDtoList(page);

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
