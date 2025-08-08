package org.example.pdnight.domain.auth.application.authUseCase;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.auth.domain.AuthCommander;
import org.example.pdnight.domain.auth.domain.AuthProducer;
import org.example.pdnight.domain.auth.domain.entity.Auth;
import org.example.pdnight.domain.auth.presentation.dto.request.LoginRequest;
import org.example.pdnight.domain.auth.presentation.dto.request.SignupRequest;
import org.example.pdnight.domain.auth.presentation.dto.request.UserPasswordUpdateRequest;
import org.example.pdnight.domain.auth.presentation.dto.request.WithdrawRequest;
import org.example.pdnight.domain.auth.presentation.dto.response.LoginResponse;
import org.example.pdnight.domain.auth.presentation.dto.response.SignupResponse;
import org.example.pdnight.domain.auth.presentation.dto.response.UserInfo;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.enums.KafkaTopic;
import org.example.pdnight.global.common.exception.BaseException;
import org.example.pdnight.global.config.PasswordEncoder;
import org.example.pdnight.global.constant.CacheName;
import org.example.pdnight.global.event.AuthDeletedEvent;
import org.example.pdnight.global.event.AuthSignedUpEvent;
import org.example.pdnight.global.utils.JwtUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthCommanderService {

    private final AuthCommander authCommander;
    private final AuthProducer producer;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserQueryPort userQueryPort;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        // auth 저장
        if (authCommander.findByEmail(request.getEmail()).isPresent()) {
            throw new BaseException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String encodePassword = passwordEncoder.encode(request.getPassword());
        Auth auth = Auth.create(
                request.getEmail(),
                encodePassword,
                null
        );

        Auth saveAuth = authCommander.save(auth);

        // 회원가입 이벤트 발행
        producer.produce(KafkaTopic.AUTH_SIGNED_UP.topicName(), new AuthSignedUpEvent(
                auth.getId(),
                request.getName(),
                request.getNickname(),
                request.getGender(),
                request.getAge(),
                request.getJobCategory()
        ));

        return SignupResponse.from(saveAuth);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Auth auth = getAuthByEmail(request.getEmail());
        validateAuth(auth, request.getPassword());

        UserInfo userInfo = userQueryPort.getUserInfoById(auth.getId());

        // 유저 나이, 성별, 직업 토큰에 담기
        String token = jwtUtil.createToken(
                auth.getId(), auth.getRole(), userInfo.getNickname(),
                userInfo.getAge(), userInfo.getGender(), userInfo.getJobCategory());

        return LoginResponse.from(token);
    }

    public void logout(HttpServletRequest http) {
        String bearerJwt = http.getHeader("Authorization");
        String token = jwtUtil.substringToken(bearerJwt);
        redisTemplate.opsForSet().add(CacheName.BLACKLIST_TOKEN, token);
    }

    @Transactional
    public void withdraw(Long userId, WithdrawRequest request) {
        Auth auth = getAuthById(userId);
        validateAuth(auth, request.getPassword());

        auth.softDelete();

        // 회원 탈퇴 이벤트 발행
        producer.produce(KafkaTopic.AUTH_DELETED.topicName(), new AuthDeletedEvent(auth.getId()));
        /*eventPublisher.publishEvent(UserDeletedEvent.of(
                auth.getId()
        ));*/
    }

    @Transactional
    public void updatePassword(Long userId, UserPasswordUpdateRequest request) {
        Auth auth = getAuthById(userId);

        // 비밀번호 검증
        validatePassword(request.getOldPassword(), auth);

        // 비밀번호 암호화
        String encodedPassword = BCrypt.withDefaults().hashToString(10, request.getNewPassword().toCharArray());
        auth.changePassword(encodedPassword);
        authCommander.save(auth);
    }

    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // get
    private Auth getAuthById(Long authId) {
        return authCommander.findById(authId)
                .orElseThrow(() -> new BaseException(ErrorCode.AUTH_NOT_FOUND));
    }

    private Auth getAuthByEmail(String email) {
        return authCommander.findByEmail(email)
                .orElseThrow(() -> new BaseException(ErrorCode.AUTH_NOT_FOUND));
    }

    // validate
    private void validateAuth(Auth auth, String password) {
        if (auth.getIsDeleted()) {
            throw new BaseException(ErrorCode.AUTH_DEACTIVATED);
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
