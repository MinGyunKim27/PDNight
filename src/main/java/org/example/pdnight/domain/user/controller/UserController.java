package org.example.pdnight.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.user.dto.request.UserUpdateRequest;
import org.example.pdnight.domain.user.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/my")
    public ApiResponse<?> getMyProfile(
            HttpServletRequest request
    ){
        // todo : @AuthenticationPrincipal를 통해 id 가져오기
        // 로그인 중인 아이디로 프로필 조회
        return ApiResponse.ok(
                "내 프로필이 조회되었습니다.",
                userService.getMyProfile(1L)
        );
    }

    @PatchMapping("my")
    public ApiResponse<?> updateMyProfile(
            HttpServletRequest request,
            @RequestBody UserUpdateRequest requestDto
    ){
        // todo : @AuthenticationPrincipal를 통해 id 가져오기
        return ApiResponse.ok(
                "프로필이 수정되었습니다.",
                userService.updateMyProfile(1L, requestDto)
        );
    }
}