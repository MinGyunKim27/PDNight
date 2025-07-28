package org.example.pdnight.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.auth.dto.request.LoginRequestDto;
import org.example.pdnight.domain.auth.dto.request.SignupRequestDto;
import org.example.pdnight.domain.auth.dto.request.WithdrawRequestDto;
import org.example.pdnight.domain.auth.dto.response.LoginResponseDto;
import org.example.pdnight.domain.auth.dto.response.SignupResponseDto;
import org.example.pdnight.domain.auth.service.AuthService;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.example.pdnight.global.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/api/auth/signup")
    private ResponseEntity<ApiResponse<SignupResponseDto>> signup(@Valid @RequestBody SignupRequestDto request) {
        SignupResponseDto user = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("회원가입 되었습니다.", user));
    }

    @PostMapping("/api/auth/login")
    private ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto request) {
        LoginResponseDto token = authService.login(request);
        return ResponseEntity.ok()
                .body(ApiResponse.ok("로그인 되었습니다.", token));
    }

    @PostMapping("/api/auth/logout")
    private ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String bearerJwt = request.getHeader("Authorization");
        String token = jwtUtil.substringToken(bearerJwt);
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.ok("로그아웃 되었습니다.", null));
    }

    @DeleteMapping("/api/auth/withdraw")
    private ResponseEntity<ApiResponse<Void>> withdraw(@Valid @RequestBody WithdrawRequestDto request,
                                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        authService.withdraw(userId, request);
        return ResponseEntity.ok(ApiResponse.ok("회원탈퇴 되었습니다.", null));
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";  // templates/login.ftl 을 찾음
    }
}
