package org.example.pdnight.domain.user.service;


import at.favre.lib.crypto.bcrypt.BCrypt;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.hobby.entity.UserHobby;
import org.example.pdnight.domain.hobby.repository.HobbyRepositoryQuery;
import org.example.pdnight.domain.techStack.entity.UserTech;
import org.example.pdnight.domain.techStack.repository.TechStackRepositoryQuery;
import org.example.pdnight.domain.user.dto.request.UserPasswordUpdateRequest;
import org.example.pdnight.domain.user.dto.request.UserUpdateRequest;
import org.example.pdnight.domain.user.dto.response.UserEvaluationResponse;
import org.example.pdnight.domain.user.dto.response.UserResponseDto;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.example.pdnight.domain.user.repository.UserRepositoryQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final HobbyRepositoryQuery hobbyRepositoryQuery;
    private final TechStackRepositoryQuery techStackRepositoryQuery;
    private final UserRepository userRepository;
    private final UserRepositoryQuery userRepositoryQuery;

    public UserResponseDto getMyProfile(Long userId) {

        // id로 유저 조회
        User user = getUserByIdWithInfo(userId);

        // UserResponseDto로 변환하여 반환
        return UserResponseDto.from(user);
    }

    @Transactional
    public UserResponseDto updateMyProfile(Long userId, UserUpdateRequest request) {
        User user = getUserByIdWithInfo(userId);

        // Set<UserHobby> / Set<UserTech> 생성 : DB 에서 있는거만 가져오기
        Set<UserHobby> userHobbies = getUserHobbyByIdList(request.getHobbyIdList(), user);

        Set<UserTech> userTechs = getUserTechByIdList(request.getTechStackIdList(), user);

        // 수정 로직
        user.updateProfile(request, userHobbies, userTechs);
        userRepository.save(user);

        return UserResponseDto.from(user);
    }

    @Transactional
    public void updatePassword(Long userId, UserPasswordUpdateRequest request) {
        User user = getUserById(userId);

        // 비밀번호 검증
        validatePassword(request.getOldPassword(), user);

        // 비밀번호 암호화
        String encodedPassword = BCrypt.withDefaults().hashToString(10, request.getNewPassword().toCharArray());
        user.changePassword(encodedPassword);
        userRepository.save(user);
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
        Page<User> users = userRepositoryQuery.searchUsers(search, pageable);
        Page<UserResponseDto> dtos = users.map(UserResponseDto::from);
        return PagedResponse.from(dtos);
    }


    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // get
    private User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new BaseException(ErrorCode.USER_NOT_FOUND));
    }

    private User getUserByIdWithInfo(Long id) {
        return userRepository.findByIdWithInfo(id).orElseThrow(
                () -> new BaseException(ErrorCode.USER_NOT_FOUND));
    }


    private void validatePassword(String old, User user) {
        boolean verified = BCrypt.verifyer()
                .verify(old.toCharArray(), user.getPassword())
                .verified;
        if (!verified) {
            throw new BaseException(ErrorCode.INVALID_PASSWORD);
        }
    }

    private Set<UserTech> getUserTechByIdList(List<Long> ids, User user) {
        Set<UserTech> userTechs = new HashSet<>();

        if (ids != null && !ids.isEmpty()) {
            userTechs = techStackRepositoryQuery.findByIdList(ids)
                    .stream()
                    .map(techStack -> UserTech.create(user, techStack))
                    .collect(Collectors.toSet());
        }
        return userTechs;
    }

    private Set<UserHobby> getUserHobbyByIdList(List<Long> ids, User user) {
        Set<UserHobby> userHobbies = new HashSet<>();

        if (ids != null && !ids.isEmpty()) {
            userHobbies = hobbyRepositoryQuery.findByIdList(ids)
                    .stream()
                    .map(hobby -> UserHobby.create(user, hobby))
                    .collect(Collectors.toSet());
        }
        return userHobbies;
    }

}
