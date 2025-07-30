package org.example.pdnight.domain1.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain1.common.dto.ApiResponse;
import org.example.pdnight.domain1.common.dto.PagedResponse;
import org.example.pdnight.domain1.user.dto.request.UserNicknameUpdateDto;
import org.example.pdnight.domain1.user.dto.response.UserResponseDto;
import org.example.pdnight.domain1.user.service.AdminUserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminUserController {
    private final AdminUserService adminUserService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponseDto>>> getAllUsers(@PageableDefault(size = 10,page = 0) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok("전체유저 조회 완료되었습니다.", adminUserService.getAllUsers(pageable)));
    }

    @PatchMapping("/users/{id}/nickname")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateNickname(@PathVariable Long id,
                                                         @RequestBody UserNicknameUpdateDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("닉네임이 변경되었습니다.", adminUserService.updateNickname(id, dto)));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.ok("회원탈퇴 시켰습니다.", null));
    }
}
