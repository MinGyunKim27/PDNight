package org.example.pdnight.domain.user.application.userUseCase;

import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.UserNicknameUpdateDto;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.UserUpdateRequest;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.FollowResponseDto;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserEvaluationResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ---------------------- Command Api -----------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//

    UserResponseDto updateMyProfile(Long userId, UserUpdateRequest request);

    FollowResponseDto follow(Long userId, Long loginId);


    void unfollow(Long userId, Long loginId);

    void delete(Long userId);
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // --------------------- Admin Command Api ------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//

    UserResponseDto updateNickname(Long userId, UserNicknameUpdateDto dto);

    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ---------------------- 조회 Api ---------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//

    UserResponseDto getMyProfile(Long userId);

    UserResponseDto getProfile(Long userId) ;

    UserEvaluationResponse getEvaluation(Long userId) ;

    //유저 이름이나 닉네임이나 이메일로 검색
    PagedResponse<UserResponseDto> searchUsers(String search, Pageable pageable);

    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // --------------------- Admin 조회 Api ----------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//

    PagedResponse<UserResponseDto> getAllUsers(Pageable pageable);
}
