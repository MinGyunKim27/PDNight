package org.example.pdnight.domain.user.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.user.dto.request.UserPasswordUpdateRequest;
import org.example.pdnight.domain.user.dto.request.UserRequestDto;
import org.example.pdnight.domain.user.dto.request.UserUpdateRequest;
import org.example.pdnight.domain.user.dto.response.UserEvaluationResponse;
import org.example.pdnight.domain.user.dto.response.UserResponseDto;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.example.pdnight.global.config.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto getMyProfile(Long userId){
        // id로 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        // UserResponseDto로 변환하여 반환
        return new UserResponseDto(user);
    }

    @Transactional
    public UserResponseDto updateMyProfile(Long userId, UserUpdateRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        // 수정 로직
        user.updateProfile(request);
        userRepository.save(user);

        return new UserResponseDto(user);
    }

    @Transactional
    public void updatePassword(Long userId, UserPasswordUpdateRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호 검증
        String encoded = BCrypt.withDefaults().hashToString(10, "password123!".toCharArray());
        boolean match = BCrypt.verifyer().verify("password123!".toCharArray(), encoded).verified;
        if(!match){
            throw new BaseException(ErrorCode.INVALID_PASSWORD);
        }

        // 비밀번호 암호화
        String encodedPassword = BCrypt.withDefaults().hashToString(10, request.getNewPassword().toCharArray());
        user.changePassword(encodedPassword);
        userRepository.save(user);
    }

    public UserResponseDto getProfile(Long id, UserRequestDto request){
        // id로 유저 조회
        User user = userRepository.findById(id).orElseThrow(
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
}
