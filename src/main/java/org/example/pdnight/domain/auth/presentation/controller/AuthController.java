package org.example.pdnight.domain.auth.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.auth.application.authUseCase.AuthService;
import org.example.pdnight.domain.auth.presentation.dto.request.LoginRequest;
import org.example.pdnight.domain.auth.presentation.dto.request.SignupRequest;
import org.example.pdnight.domain.auth.presentation.dto.request.UserPasswordUpdateRequest;
import org.example.pdnight.domain.auth.presentation.dto.request.WithdrawRequest;
import org.example.pdnight.domain.auth.presentation.dto.response.LoginResponse;
import org.example.pdnight.domain.auth.presentation.dto.response.SignupResponse;
import org.example.pdnight.global.common.dto.ApiResponse;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth")
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "사용자가 서비스에 가입한다")
    private ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse user = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("회원가입 되었습니다.", user));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자가 서비스에 접속한다")
    private ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse token = authService.login(request);
        return ResponseEntity.ok()
                .body(ApiResponse.ok("로그인 되었습니다.", token));
    }

    @PatchMapping("/password")
    @Operation(summary = "비밀번호 변경", description = "본인의 비밀번호를 변경한다")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserPasswordUpdateRequest requestDto
    ) {
        Long userId = userDetails.getUserId();
        authService.updatePassword(userId, requestDto);

        return ResponseEntity.ok(ApiResponse.ok(
                "비밀번호가 수정되었습니다.",
                null
        ));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "서비스를 접속 해제한다")
    private ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest http) {
        authService.logout(http);
        return ResponseEntity.ok(ApiResponse.ok("로그아웃 되었습니다.", null));
    }

    @DeleteMapping("/withdraw")
    @Operation(summary = "회원탈퇴", description = "사용자가 서비스를 탈퇴한다")
    private ResponseEntity<ApiResponse<Void>> withdraw(HttpServletRequest http,
                                                       @Valid @RequestBody WithdrawRequest request,
                                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        authService.withdraw(userId, request);
        authService.logout(http);
        return ResponseEntity.ok(ApiResponse.ok("회원탈퇴 되었습니다.", null));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<LoginResponse>> reissue(@RequestHeader("Authorization") String refreshTokenHeader) {
        log.info("리이슈 리프레쉬 토큰" + refreshTokenHeader);
        LoginResponse response = authService.reissue(refreshTokenHeader);
        return ResponseEntity.ok(ApiResponse.ok("토큰 재발급 성공", response));
    }

}
