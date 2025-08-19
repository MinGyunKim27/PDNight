package org.example.pdnight.domain.auth.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "OAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OAuthController {

    private final OAuthService oAuthService;

    @GetMapping("/login/google")
    @Operation(summary = "OAuth GooGle 로그인", description = "사용자가 OAuth Google로 로그인 시도")
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
        String authorizationUrl = oAuthService.getAuthorizationUrl();
        response.sendRedirect(authorizationUrl);
    }

    @GetMapping("/callback/google")
    @Operation(summary = "OAuth GooGle 로그인 콜백", description = "Google로 로그인성공 콜백")
    public ResponseEntity<ApiResponse<OAuthLoginResponse>> googleCallback(
            @RequestParam String code,
            @RequestParam String state
    ) {
        return ResponseEntity.ok(ApiResponse.ok("OAuth 로그인에 성공 하였습니다" , oAuthService.loginWithOAuth(code, state))  );
    }
}

