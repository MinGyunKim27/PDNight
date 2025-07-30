package org.example.pdnight.domain.user.application.userUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.user.domain.userDomain.UserReader;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.FollowingResponseDto;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserEvaluationResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserQueryService {

    private final UserReader userReader;

    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ---------------------- 조회 Api ---------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//


    public UserResponseDto getMyProfile(Long userId) {

        // id로 유저 조회
        User user = getUserByIdWithInfo(userId);

        // UserResponseDto로 변환하여 반환
        return UserResponseDto.from(user);
    }



    public UserResponseDto getProfile(Long userId) {
        // id로 유저 조회
        User user = getUserByIdWithInfo(userId);

        // UserResponseDto로 변환하여 반환
        return UserResponseDto.from(user);

    }

    public UserEvaluationResponse getEvaluation(Long userId) {
        // id로 유저 조회
        User user = getUserById(userId);

        return UserEvaluationResponse.from(user);
    }

    //유저 이름이나 닉네임이나 이메일로 검색
    public PagedResponse<UserResponseDto> searchUsers(String search, Pageable pageable) {
        Page<User> users = userReader.searchUsers(search, pageable);
        Page<UserResponseDto> dtos = users.map(UserResponseDto::from);
        return PagedResponse.from(dtos);
    }

    public Page<FollowingResponseDto> getFollowings(Long myId, Pageable pageable) {
        return userReader.findFollowingsByUserId(myId, pageable);
    }

    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // --------------------- Admin 조회 Api ----------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//

    @Transactional(readOnly = true)
    public PagedResponse<UserResponseDto> getAllUsers(Pageable pageable) {

        Page<User> users = userReader.findAll(pageable);

        return PagedResponse.from(users.map(UserResponseDto::from));
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

    private User getUserByIdWithInfo(Long id) {
        return userReader.findById(id).orElseThrow(
                () -> new BaseException(ErrorCode.USER_NOT_FOUND));
    }

}
