package org.example.pdnight.domain.user.application.userUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.UserNicknameUpdateDto;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.UserUpdateRequest;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.FollowResponseDto;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserEvaluationResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserResponseDto;
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

    public UserResponseDto updateMyProfile(Long userId, UserUpdateRequest request){
        return userCommandService.updateMyProfile(userId,request);
    }


    public FollowResponseDto follow(Long userId, Long loginId){
        return userCommandService.follow(userId,loginId);
    }


    public void unfollow(Long userId, Long loginId){
        userCommandService.unfollow(userId,loginId);
    }

    public void delete(Long userId){
        userCommandService.delete(userId);
    }
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // --------------------- Admin Command Api ------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//

    public UserResponseDto updateNickname(Long userId, UserNicknameUpdateDto dto){
        return userCommandService.updateNickname(userId,dto);
    }

    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ---------------------- 조회 Api ---------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//

    public UserResponseDto getMyProfile(Long userId){
        return userQueryService.getMyProfile(userId);
    }

    public UserResponseDto getProfile(Long userId) {
        return userQueryService.getProfile(userId);
    }

    public UserEvaluationResponse getEvaluation(Long userId) {
        return userQueryService.getEvaluation(userId);
    }

    //유저 이름이나 닉네임이나 이메일로 검색
    public PagedResponse<UserResponseDto> searchUsers(String search, Pageable pageable){
        return userQueryService.searchUsers(search,pageable);
    }

    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // --------------------- Admin 조회 Api ----------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//

    public PagedResponse<UserResponseDto> getAllUsers(Pageable pageable){
        return userQueryService.getAllUsers(pageable);
    }

}
