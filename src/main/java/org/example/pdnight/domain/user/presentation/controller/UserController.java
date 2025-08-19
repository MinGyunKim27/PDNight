package org.example.pdnight.domain.user.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.application.userUseCase.UserService;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.GiveCouponRequest;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.UserNicknameUpdate;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.UserUpdateRequest;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.*;
import org.example.pdnight.global.common.dto.ApiResponse;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // -------------------------- Command Api -------------------------------------------------//
    // --------------- users
    // 본인 프로필 수정
    @PatchMapping("/users/my/profile")
    @Operation(summary = "프로필 수정", description = "본인의 프로필을 수정한다", tags = {"User"})
    public ResponseEntity<ApiResponse<UserResponse>> updateMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserUpdateRequest request
    ) {
        Long userId = userDetails.getUserId();

        return ResponseEntity.ok(ApiResponse.ok(
                "프로필이 수정되었습니다.",
                userService.updateMyProfile(userId, request)
        ));
    }

    // --------------- users/follow
    //팔로우
    @PostMapping("/users/{userId}/follow")
    @Operation(summary = "팔로우", description = "상대방을 팔로우한다", tags = {"Follow"})
    public ResponseEntity<ApiResponse<FollowResponse>> follow(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        FollowResponse follow = userService.follow(userId, loginUser.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("팔로우 했습니다.", follow));
    }

    //언팔로우
    @DeleteMapping("/users/{userId}/follow")
    @Operation(summary = "언팔로우", description = "팔로우를 취소한다", tags = {"Follow"})
    public ResponseEntity<ApiResponse<Void>> unfollow(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails loggedInUser
    ) {
        userService.unfollow(userId, loggedInUser.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("언팔로우 했습니다.", null));
    }

    // --------------- coupons
    // 쿠폰사용
    @PatchMapping("/users/user-coupons/{id}")
    @Operation(summary = "쿠폰 사용", description = "본인의 쿠폰을 사용한다", tags = {"Coupon"})
    public ResponseEntity<ApiResponse<UserCouponResponse>> useCoupon(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(ApiResponse.ok("쿠폰을 사용하였습니다.", userService.useCoupon(id, userDetails.getUserId())));
    }


    // --------------------- Admin Command Api ------------------------------------------------//
    // --------------- users
    @PatchMapping("/admin/users/{id}/nickname")
    @Operation(summary = "[관리자] 닉네임 강제 변경", description = "관리자가 사용자의 닉네임을 강제로 변경한다", tags = {"AdminUser"})
    public ResponseEntity<ApiResponse<UserResponse>> adminUpdateNickname(@PathVariable Long id,
                                                                         @RequestBody UserNicknameUpdate dto) {
        return ResponseEntity.ok(ApiResponse.ok("닉네임이 변경되었습니다.", userService.updateNickname(id, dto)));
    }

    @DeleteMapping("/admin/users/{id}")
    @Operation(summary = "[관리자] 강제 회원탈퇴", description = "관리자가 사용자를 회원탈퇴 시킨다", tags = {"AdminUser"})
    public ResponseEntity<ApiResponse<Void>> adminDeleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("회원탈퇴 시켰습니다.", null));
    }

    // --------------- coupons
    // 사용자에게 쿠폰 부여
    @PostMapping("/admin/users/coupons")
    @Operation(summary = "[관리자] 사용자에게 쿠폰 부여", description = "관리자가 사용자에게 쿠폰을 부여한다", tags = {"AdminCoupon"})
    public ResponseEntity<ApiResponse<UserCouponResponse>> giveCouponToUser(
            @RequestBody GiveCouponRequest request
    ) {
        UserCouponResponse response = userService.giveCouponToUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("쿠폰이 등록되었습니다.", response));
    }

    // ---------------------- 조회 Api ---------------------------------------------------------//
    // --------------- users
    // 내 프로필 조회
    @GetMapping("/users/my/profile")
    @Operation(summary = "내 프로필 조회", description = "본인의 프로필을 조회한다", tags = {"User"})
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        return ResponseEntity.ok(ApiResponse.ok(
                "내 프로필이 조회되었습니다.",
                userService.getMyProfile(userId)
        ));
    }

    // 유저 프로필 조회
    @GetMapping("/users/{id}/profile")
    @Operation(summary = "사용자 프로필 조회", description = "다른 사용자의 프로필을 조회한다", tags = {"User"})
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                "프로필이 조회되었습니다.",
                userService.getProfile(id)
        ));
    }

    //유저 검색
    @GetMapping("/users/search")
    @Operation(summary = "사용자 검색 조회", description = "사용자를 검색해서 조회한다", tags = {"User"})
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> searchUsers(
            @RequestParam String search,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                "유저 검색이 완료 되었습니다.",
                userService.searchUsers(search, pageable)
        ));
    }

    //평가 조회
    @GetMapping("/users/{id}/evaluation")
    @Operation(summary = "사용자 평가 조회", description = "사용자의 평가를 조회한다", tags = {"User"})
    public ResponseEntity<ApiResponse<UserEvaluationResponse>> getEvaluation(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                "사용자 평가가 조회되었습니다.",
                userService.getEvaluation(id)
        ));
    }

    // --------------- users/follow
    //내 팔로잉 목록 조회
    @GetMapping("/users/my/following")
    @Operation(summary = "내 팔로잉 목록 조회", description = "본인의 팔로잉 목록을 조회한다", tags = {"Follow"})
    public ResponseEntity<ApiResponse<PagedResponse<FollowingResponse>>> getFollowings(
            @AuthenticationPrincipal CustomUserDetails loggedInUser,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long myId = loggedInUser.getUserId();
        PagedResponse<FollowingResponse> followings = userService.getFollowings(myId, pageable);

        return ResponseEntity.ok(ApiResponse.ok("팔로잉 목록 조회가 완료되었습니다.", followings));
    }

    // 내 쿠폰목록 조회
    @GetMapping("/users/my/user-coupons")
    @Operation(summary = "내 쿠폰 목록 조회", description = "본인의 쿠폰 목록을 조회한다", tags = {"Coupon"})
    public ResponseEntity<ApiResponse<PagedResponse<UserCouponResponse>>> getMyCoupons(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault() Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok("쿠폰이 조회되었습니다.", userService.getValidCoupons(userDetails.getUserId(), pageable)));
    }

    // --------------------- Admin 조회 Api ----------------------------------------------------//
    // --------------- users
    @GetMapping("/admin/users")
    @Operation(summary = "[관리자] 전체 사용자 조회", description = "서비스에 가입한 사용자 목록을 조회한다", tags = {"AdminUser"})
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> adminGetAllUsers(
            @PageableDefault() Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok("전체유저 조회 완료되었습니다.", userService.getAllUsers(pageable)));
    }

}
