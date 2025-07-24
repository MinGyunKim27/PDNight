package org.example.pdnight.domain.user.service;


import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.hobby.entity.Hobby;
import org.example.pdnight.domain.hobby.repository.HobbyRepository;
import org.example.pdnight.domain.hobby.repository.HobbyRepositoryQuery;
import org.example.pdnight.domain.techStack.entity.TechStack;
import org.example.pdnight.domain.techStack.repository.TechStackRepository;
import org.example.pdnight.domain.hobby.repository.HobbyRepositoryQuery;
import org.example.pdnight.domain.techStack.repository.TechStackRepositoryQuery;
import org.example.pdnight.domain.hobby.entity.UserHobby;
import org.example.pdnight.domain.techStack.entity.UserTech;
import at.favre.lib.crypto.bcrypt.BCrypt;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final HobbyRepository hobbyRepository;
    private final TechStackRepository techStackRepository;
    private final HobbyRepositoryQuery hobbyRepositoryQuery;
    private final TechStackRepositoryQuery techStackRepositoryQuery;
    private final UserRepository userRepository;
    private final UserRepositoryQuery userRepositoryQuery;

    public UserResponseDto getMyProfile(Long userId){
        // id로 유저 조회
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        // UserResponseDto로 변환하여 반환
        return new UserResponseDto(user);
    }

    @Transactional
    public UserResponseDto updateMyProfile(Long userId, UserUpdateRequest request){
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        // Set<UserHobby> / Set<UserTech> 생성 : DB 에서 있는거만 가져오기
        Set<UserHobby> userHobbies = new HashSet<>();
        if (request.getHobbyIdList() != null && !request.getHobbyIdList().isEmpty()) {
            userHobbies = hobbyRepositoryQuery.findByIdList(request.getHobbyIdList())
                    .stream()
                    .map(hobby -> new UserHobby(user, hobby))
                    .collect(Collectors.toSet());
        }
        Set<UserTech> userTechs = new HashSet<>();
        if (request.getTechStackIdList() != null && !request.getTechStackIdList().isEmpty()) {
            userTechs = techStackRepositoryQuery.findByIdList(request.getTechStackIdList())
                    .stream()
                    .map(techStack -> new UserTech(user, techStack))
                    .collect(Collectors.toSet());
        }

        // 수정 로직
        user.updateProfile(request, userHobbies, userTechs);
        userRepository.save(user);

        return new UserResponseDto(user);
    }

    @Transactional
    public void updatePassword(Long userId, UserPasswordUpdateRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호 검증
        boolean match = BCrypt.verifyer()
                .verify(request.getOldPassword().toCharArray(), user.getPassword())
                .verified;
        if (!match) {
            throw new BaseException(ErrorCode.INVALID_PASSWORD);
        }

        // 비밀번호 암호화
        String encodedPassword = BCrypt.withDefaults().hashToString(10, request.getNewPassword().toCharArray());
        user.changePassword(encodedPassword);
        userRepository.save(user);
    }

    public UserResponseDto getProfile(Long id){
        // id로 유저 조회
        User user = userRepository.findUserById(id).orElseThrow(
                ()-> new BaseException(ErrorCode.USER_NOT_FOUND));

        // UserResponseDto로 변환하여 반환
        return new UserResponseDto(user);

    }

    public UserEvaluationResponse getEvaluation(Long id){
        // id로 유저 조회
        User user = userRepository.findById(id).orElseThrow(
                ()-> new BaseException(ErrorCode.USER_NOT_FOUND));

        return new UserEvaluationResponse(user);
    }

    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(
                ()-> new BaseException(ErrorCode.USER_NOT_FOUND));
    }

    //유저 이름이나 닉네임이나 이메일로 검색
    public PagedResponse<UserResponseDto> searchUsers(String search, Pageable pageable) {
        Page<User> users = userRepositoryQuery.searchUsers(search,pageable);
        Page<UserResponseDto> dtos = users.map(UserResponseDto::new);
        return PagedResponse.from(dtos);
    }
}
