package org.example.pdnight.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.auth.dto.request.LoginRequestDto;
import org.example.pdnight.domain.auth.dto.request.SignupRequestDto;
import org.example.pdnight.domain.auth.dto.request.WithdrawRequestDto;
import org.example.pdnight.domain.auth.dto.response.LoginResponseDto;
import org.example.pdnight.domain.auth.dto.response.SignupResponseDto;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.hobby.entity.Hobby;
import org.example.pdnight.domain.hobby.repository.HobbyRepository;
import org.example.pdnight.domain.techStack.entity.TechStack;
import org.example.pdnight.domain.techStack.repository.TechStackRepository;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.example.pdnight.global.config.PasswordEncoder;
import org.example.pdnight.global.utils.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final HobbyRepository hobbyRepository;
    private final TechStackRepository techStackRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public SignupResponseDto signup(SignupRequestDto request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BaseException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 취미, 기술 스택 입력은 db에 존재하는 값만 입력받는 조건
        Hobby hobby = null;
        String hobbyStr = null;
        if (request.getHobby() != null) {
            hobby = hobbyRepository.findByhobby(request.getHobby());
            hobbyStr = hobby.getHobby();
        }

        TechStack techStack = null;
        String techStackStr = null;
        if (request.getTechStack() != null) {
            techStack = techStackRepository.findByTechStack(request.getTechStack());
            techStackStr = techStack.getTechStack();
        }

        String encodePassword = passwordEncoder.encode(request.getPassword());
        User user = new User(request, encodePassword, hobby, techStack);

        User saveUser = userRepository.save(user);

        return new SignupResponseDto(saveUser, hobbyStr, techStackStr);

    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        if(user.getIsDeleted()) {
            throw new BaseException(ErrorCode.USER_DEACTIVATED);
        }

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BaseException(ErrorCode.INVALID_PASSWORD);
        }

        String token = jwtUtil.createToken(user.getId(), user.getRole());
        return new LoginResponseDto(token);
    }

    public void logout(Long userId) {

    }

    @Transactional
    public void withdraw(Long userId, WithdrawRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        if(user.getIsDeleted()) {
            throw new BaseException(ErrorCode.USER_DEACTIVATED);
        }

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BaseException(ErrorCode.INVALID_PASSWORD);
        }

        user.softDelete();
    }

}
