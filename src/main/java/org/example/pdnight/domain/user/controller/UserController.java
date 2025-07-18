package org.example.pdnight.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/my")
    public ApiResponse<?> getMyProfile(
            HttpServletRequest request
    ){
        // 회원가입 기능 도입 후 아이디 읽어오도록 수정
        // 로그인 중인 아이디로 프로필 조회
        return ApiResponse.ok(
                "내 프로필이 조회되었습니다.",
                userService.getMyProfile(1L)
        );
    }
}