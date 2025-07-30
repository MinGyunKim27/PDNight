package org.example.pdnight.domain.auth.application.authUseCase;

import at.favre.lib.crypto.bcrypt.BCrypt;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.auth.domain.AuthCommandQuery;
import org.example.pdnight.domain.auth.domain.AuthReader;
import org.example.pdnight.domain.auth.domain.entity.Auth;
import org.example.pdnight.domain.auth.presentation.dto.request.LoginRequestDto;
import org.example.pdnight.domain.auth.presentation.dto.request.SignupRequestDto;
import org.example.pdnight.domain.auth.presentation.dto.request.UserPasswordUpdateRequest;
import org.example.pdnight.domain.auth.presentation.dto.request.WithdrawRequestDto;
import org.example.pdnight.domain.auth.presentation.dto.response.LoginResponseDto;
import org.example.pdnight.domain.auth.presentation.dto.response.SignupResponseDto;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.user.application.event.UserRegisteredEvent;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.userDomain.UserReader;
import org.example.pdnight.global.config.PasswordEncoder;
import org.example.pdnight.global.constant.CacheName;
import org.example.pdnight.global.utils.JwtUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthCommandService {

    private final AuthReader authReader;
    private final AuthCommandQuery authCommandQuery;
    private final ApplicationEventPublisher eventPublisher;
    private final UserReader userReader;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public SignupResponseDto signup(SignupRequestDto request) {
        // auth 저장
        if (getAuthByEmail(request.getEmail()) != null) {
            throw new BaseException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String encodePassword = passwordEncoder.encode(request.getPassword());
        Auth auth = Auth.create(
                request.getEmail(),
                encodePassword,
                null
        );

        Auth saveAuth = authCommandQuery.save(auth);

        // 회원가입 이벤트 발행
        eventPublisher.publishEvent(UserRegisteredEvent.of(
                auth.getId(),
                request
        ));

        return SignupResponseDto.from(saveAuth);
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {
        Auth auth = getAuthByEmail(request.getEmail());

        validateUser(auth, request.getPassword());  // 컨트롤러 호출
        // 유저 나이, 성별, 직업

        String token = jwtUtil.createToken(auth.getId(), auth.getRole(), user.getNickname());
        return LoginResponseDto.from(token);
    }

    public void logout(String token) {
        redisTemplate.opsForSet().add(CacheName.BLACKLIST_TOKEN, token);
    }

    @Transactional
    public void withdraw(Long userId, WithdrawRequestDto request) {
        User user = getUserById(userId); // 컨트롤러 호출

        validateUser(user, request.getPassword());

        user.softDelete();
    }

    @Transactional
    public void updatePassword(Long userId, UserPasswordUpdateRequest request) {
        User user = getUserById(userId);

        // 비밀번호 검증
        validatePassword(request.getOldPassword(), user);

        // 비밀번호 암호화
        String encodedPassword = BCrypt.withDefaults().hashToString(10, request.getNewPassword().toCharArray());
        user.changePassword(encodedPassword);
        userCommandQuery.save(user);
    }

    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // get
    private User getUserById(Long userId) {
        return userReader.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
    }

    private Auth getAuthByEmail(String email) {
        return authReader.findByEmail(email)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
    }

    // validate
    private void validateUser(Auth auth, String password) {
        //

        if (auth.getIsDeleted()) {
            throw new BaseException(ErrorCode.USER_DEACTIVATED);
        }

        if (!passwordEncoder.matches(password, auth.getPassword())) {
            throw new BaseException(ErrorCode.INVALID_PASSWORD);
        }
    }

    // ----------- 중간 테이블 용 Helper 메서드 --------------------------//

    private void validatePassword(String old, Auth auth) {
        boolean verified = BCrypt.verifyer()
                .verify(old.toCharArray(), auth.getPassword())
                .verified;
        if (!verified) {
            throw new BaseException(ErrorCode.INVALID_PASSWORD);
        }
    }
}
