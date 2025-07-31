package org.example.pdnight.domain.user.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.enums.JoinStatus;
import org.example.pdnight.domain.invite.dto.response.InviteResponseDto;
import org.example.pdnight.domain.invite.service.InviteService;
import org.example.pdnight.domain.post.dto.response.PostResponseWithApplyStatusDto;
import org.example.pdnight.domain.post.dto.response.PostWithJoinStatusAndAppliedAtResponseDto;
import org.example.pdnight.domain.post.service.PostService;
import org.example.pdnight.domain.user.application.userUseCase.UserService;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.GiveCouponRequest;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.UserNicknameUpdate;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.UserUpdateRequest;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.*;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.data.domain.Page;
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
    private final PostService postService;
    private final InviteService inviteService;

    // -------------------------- Command Api -------------------------------------------------//

    // 본인 프로필 수정
    @PatchMapping("/users/my/profile")
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

    //팔로우
    @PostMapping("/api/users/{userId}/follow")
    public ResponseEntity<ApiResponse<FollowResponse>> follow(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        FollowResponse follow = userService.follow(userId, loginUser.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("팔로우 했습니다.", follow));
    }

    //언팔로우
    @DeleteMapping("/api/users/{userId}/follow")
    public ResponseEntity<ApiResponse<Void>> unfollow(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails loggedInUser
    ) {
        userService.unfollow(userId, loggedInUser.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("언팔로우 했습니다.", null));
    }

    // 사용자에게 쿠폰 부여
    @PostMapping("/admin/coupons/{id}")
    public ResponseEntity<ApiResponse<UserCouponResponse>> giveCouponToUser(
            @RequestBody GiveCouponRequest request
    ) {
        UserCouponResponse response = userService.giveCouponToUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("쿠폰이 등록되었습니다.", response));
    }

    // 쿠폰사용
    @PatchMapping("/coupons/{id}")
    public ResponseEntity<ApiResponse<UserCouponResponse>> useCoupon(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(ApiResponse.ok("쿠폰을 사용하였습니다.", userService.useCoupon(id, userDetails.getUserId())));
    }


    // --------------------- Admin Command Api ------------------------------------------------//

    @PatchMapping("/admin/users/{id}/nickname")
    public ResponseEntity<ApiResponse<UserResponse>> adminUpdateNickname(@PathVariable Long id,
                                                                         @RequestBody UserNicknameUpdate dto) {
        return ResponseEntity.ok(ApiResponse.ok("닉네임이 변경되었습니다.", userService.updateNickname(id, dto)));
    }

    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<ApiResponse<Void>> adminDeleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("회원탈퇴 시켰습니다.", null));
    }

    // ---------------------- 조회 Api ---------------------------------------------------------//

    //todo: 얘는 포스트 이동?
    // 내 좋아요 게시글 목록 조회
    @GetMapping("/users/my/likedPosts")
    public ResponseEntity<ApiResponse<PagedResponse<PostResponseWithApplyStatusDto>>> getMyLikedPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault() Pageable pageable
    ) {
        Long id = userDetails.getUserId();
        PagedResponse<PostResponseWithApplyStatusDto> myLikedPost = postService.findMyLikedPosts(id, pageable);
        return ResponseEntity.ok(ApiResponse.ok("내 좋아요 게시글 목록이 조회되었습니다.", myLikedPost));
    }

    //todo: 얘는 포스트 이동?
    //내 신청/성사된 게시글 조회
    @GetMapping("/users/my/confirmedPosts")
    public ResponseEntity<ApiResponse<PagedResponse<PostWithJoinStatusAndAppliedAtResponseDto>>> getMyConfirmedPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) JoinStatus joinStatus,
            @PageableDefault() Pageable pageable
    ) {
        Long id = userDetails.getUserId();
        PagedResponse<PostWithJoinStatusAndAppliedAtResponseDto> myLikedPost = postService.findMyConfirmedPosts(id, joinStatus, pageable);
        return ResponseEntity.ok(ApiResponse.ok("참여 신청한 게시글 목록이 조회되었습니다.", myLikedPost));
    }

    //todo: 얘는 포스트 이동?
    // 내가 작성한 게시글 조회
    @GetMapping("/users/my/writtenPosts")
    public ResponseEntity<ApiResponse<PagedResponse<PostResponseWithApplyStatusDto>>> getMyWrittenPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault() Pageable pageable
    ) {
        Long id = userDetails.getUserId();
        PagedResponse<PostResponseWithApplyStatusDto> myLikedPost = postService.findMyWrittenPosts(id, pageable);
        return ResponseEntity.ok(ApiResponse.ok("내가 작성 한 게시물이 조회되었습니다.", myLikedPost));
    }

    // 내 프로필 조회
    @GetMapping("/users/my/profile")
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
    public ResponseEntity<ApiResponse<UserEvaluationResponse>> getEvaluation(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                "사용자 평가가 조회되었습니다.",
                userService.getEvaluation(id)
        ));
    }

    //내 팔로잉 목록 조회
    @GetMapping("/users/my/following")
    public ResponseEntity<ApiResponse<PagedResponse<FollowingResponse>>> getFollowings(
            @AuthenticationPrincipal CustomUserDetails loggedInUser,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long myId = loggedInUser.getUserId();
        Page<FollowingResponse> followings = userService.getFollowings(myId, pageable);
        PagedResponse<FollowingResponse> dtoPagedResponse = PagedResponse.from(followings);

        return ResponseEntity.ok(ApiResponse.ok("팔로잉 목록 조회가 완료되었습니다.", dtoPagedResponse));
    }

    // 내 쿠폰목록 조회
    @GetMapping("/users/my/coupons")
    public ResponseEntity<ApiResponse<PagedResponse<UserCouponResponse>>> getMyCoupons(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault() Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok("쿠폰이 조회되었습니다.", userService.getValidCoupons(userDetails.getUserId(), pageable)));
    }

    // --------------------- Admin 조회 Api ----------------------------------------------------//

    @GetMapping("/admin/users")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> adminGetAllUsers(
            @PageableDefault() Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok("전체유저 조회 완료되었습니다.", userService.getAllUsers(pageable)));
    }

    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------- 내 초대 API -----------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//

    //todo: Invite로 이동 해야함.
    //내 초대받은 목록 조회
    @GetMapping("/users/my/invited")
    public ResponseEntity<ApiResponse<PagedResponse<InviteResponseDto>>> getMyInvited(
            @AuthenticationPrincipal CustomUserDetails loggedInUser,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long userId = loggedInUser.getUserId();
        PagedResponse<InviteResponseDto> inviteResponseDto = inviteService.getMyInvited(userId, pageable);

        return ResponseEntity.ok(ApiResponse.ok("초대 받은 목록 조회가 완료되었습니다", inviteResponseDto));
    }

    //내가 보낸 초대 목록 조회
    @GetMapping("/users/my/invite")
    public ResponseEntity<ApiResponse<PagedResponse<InviteResponseDto>>> getMyInvite(
            @AuthenticationPrincipal CustomUserDetails loggedInUser,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long userId = loggedInUser.getUserId();
        PagedResponse<InviteResponseDto> inviteResponseDto = inviteService.getMyInvite(userId, pageable);

        return ResponseEntity.ok(ApiResponse.ok("초대 받은 목록 조회가 완료되었습니다", inviteResponseDto));
    }
}
