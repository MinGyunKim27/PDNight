package org.example.pdnight.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.user.dto.request.UserPasswordUpdateRequest;
import org.example.pdnight.domain.user.dto.request.UserUpdateRequest;
import org.example.pdnight.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<?>> getMyProfile(
            HttpServletRequest request
    ){
        Long userId = (Long) request.getAttribute("userId");
        // 로그인 중인 아이디로 프로필 조회
        return ResponseEntity.ok(ApiResponse.ok(
                "내 프로필이 조회되었습니다.",
                userService.getMyProfile(userId)
        ));
    }

    @PatchMapping("/my/profile")
    public ResponseEntity<ApiResponse<?>> updateMyProfile(
            HttpServletRequest request,
            @RequestBody UserUpdateRequest requestDto
    ){
        Long userId = (Long) request.getAttribute("userId");

        return ResponseEntity.ok(ApiResponse.ok(
                "프로필이 수정되었습니다.",
                userService.updateMyProfile(userId, requestDto)
        ));
    }

    @PatchMapping("/my/password")
    public ResponseEntity<ApiResponse<?>> updatePassword(
            HttpServletRequest request,
            @Valid @RequestBody UserPasswordUpdateRequest requestDto
    ){
        Long userId = (Long) request.getAttribute("userId");
        userService.updatePassword(userId, requestDto);

        return ResponseEntity.ok(ApiResponse.ok(
                "비밀번호가 수정되었습니다.",
                null
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getProfile(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(ApiResponse.ok(
                "프로필이 조회되었습니다.",
                userService.getProfile(id, null)
        ));
    }

    @GetMapping("/{id}/evaluation")
    public ResponseEntity<ApiResponse<?> >getEvaluation(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(ApiResponse.ok(
                "사용자 평가가 조회되었습니다.",
                userService.getEvaluation(id)
        ));
    }
}