package org.example.pdnight.domain.auth.presentation.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.auth.application.authUseCase.OAuthService;
import org.example.pdnight.domain.auth.presentation.dto.response.OAuthLoginResponse;
import org.example.pdnight.global.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OAuthController {

    private final OAuthService oAuthService;

    @GetMapping("/login/google")
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
        String authorizationUrl = oAuthService.getAuthorizationUrl();
        response.sendRedirect(authorizationUrl);
    }

    @GetMapping("/callback/google")
    public ResponseEntity<ApiResponse<OAuthLoginResponse>> googleCallback(
            @RequestParam String code,
            @RequestParam String state
    ) {
        return ResponseEntity.ok(ApiResponse.ok("OAuth 로그인에 성공 하였습니다" , oAuthService.loginWithOAuth(code, state))  );
    }
}

