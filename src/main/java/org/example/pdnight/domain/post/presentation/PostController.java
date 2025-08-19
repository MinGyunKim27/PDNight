package org.example.pdnight.domain.post.presentation;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.application.PostUseCase.PostService;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.domain.post.presentation.dto.request.PostRequest;
import org.example.pdnight.domain.post.presentation.dto.request.PostStatusRequest;
import org.example.pdnight.domain.post.presentation.dto.request.PostUpdateRequest;
import org.example.pdnight.domain.post.presentation.dto.response.InviteResponse;
import org.example.pdnight.domain.post.presentation.dto.response.ParticipantResponse;
import org.example.pdnight.domain.post.presentation.dto.response.PostLikeResponse;
import org.example.pdnight.domain.post.presentation.dto.response.PostResponse;
import org.example.pdnight.global.common.dto.ApiResponse;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.example.pdnight.global.common.enums.JobCategory;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    //region 게시글
    //region 게시글 조회 제외 메서드들
    // 게시글 생성
    @PostMapping("/posts")
    @Operation(summary = "게시글 등록", description = "게시글을 등록한다", tags = {"Post"})
    public ResponseEntity<ApiResponse<PostResponse>> savePost(
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long userId = loginUser.getUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("정상적으로 등록되었습니다.", postService.createPost(userId, request)));
    }

    // 게시글 수정
    @PatchMapping("/posts/{id}")
    @Operation(summary = "게시글 수정", description = "게시글을 수정한다", tags = {"Post"})
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @PathVariable Long id,
            @RequestBody PostUpdateRequest requestDto,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long userId = loginUser.getUserId();
        PostResponse updatedPost = postService.updatePostDetails(userId, id, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("게시글이 수정되었습니다.", updatedPost));
    }

    // 게시글 상태 수정
    @PatchMapping("/posts/{id}/status")
    @Operation(summary = "게시글 상태 수정", description = "게시글 상태를 수정한다", tags = {"Post"})
    public ResponseEntity<ApiResponse<PostResponse>> updateStatus(
            @PathVariable Long id,
            @RequestBody PostStatusRequest requestDto,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long userId = loginUser.getUserId();
        PostResponse updatedPost = postService.changeStatus(userId, id, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("게시글 상태가 수정되었습니다.", updatedPost));
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{id}")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제한다", tags = {"Post"})
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long userId = loginUser.getUserId();
        postService.deletePostById(userId, id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("게시글이 삭제되었습니다.", null));
    }

    // 어드민 - 유저게시글 삭제
    @DeleteMapping("/admin/posts/{id}")
    @Operation(summary = "[관리자] 게시글 강제 삭제", description = "관리자가 게시글을 강제로 삭제한다", tags = {"AdminPost"})
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long id
    ) {
        postService.deleteAdminPostById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("게시글이 삭제되었습니다.", null));
    }

    //endregion
    //region 게시글조회
    //추천 게시글 조회
    @GetMapping("/posts/suggested-posts")
    @Operation(summary = "추천 게시글 조회", description = "추천 게시글 목록을 조회한다", tags = {"Post"})
    public ResponseEntity<ApiResponse<PagedResponse<PostResponse>>> suggestedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Long userId = loginUser.getUserId();
        PagedResponse<PostResponse> pagedResponse = postService.getSuggestedPosts(userId, pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("게시글 목록이 조회되었습니다.", pagedResponse));
    }


    //endregion
    //endregion
    //region 게시글신청자
    //region 게시글신청자 조회제외 메서드들
    // 게시글 참여 신청
    @PostMapping("/posts/{postId}/participate")
    @Operation(summary = "게시글 참여 신청", description = "게시글에 참여 신청한다", tags = {"Participate"})
    public ResponseEntity<ApiResponse<ParticipantResponse>> applyParticipant(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @PathVariable Long postId
    ) {
        ParticipantResponse response = postService.applyParticipant(loginUser.getUserId(),
                loginUser.getAge(),
                loginUser.getGender(),
                loginUser.getJobCategory(),
                postId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("참여 신청되었습니다.", response));
    }

    // 게시글 신청 수락 or 거절
    @PatchMapping("/posts/{postId}/participate/users/{userId}")
    @Operation(summary = "게시글 신청 수락/거절", description = "게시글 신청자를 수락하거나 거절한다", tags = {"Participate"})
    public ResponseEntity<ApiResponse<ParticipantResponse>> changeStatusParticipant(
            @AuthenticationPrincipal CustomUserDetails author,
            @PathVariable Long postId,
            @PathVariable Long userId,
            @RequestParam String status
    ) {
        ParticipantResponse response = postService.changeStatusParticipant(author.getUserId(), userId, postId, status);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("신청자의 상태가 변경되었습니다.", response));
    }

    // 게시글 참여 신청 삭제
    @DeleteMapping("/posts/{postId}/participate")
    @Operation(summary = "게시글 신청 삭제", description = "본인이 참여 신청한 것을 취소한다", tags = {"Participate"})
    public ResponseEntity<ApiResponse<Void>> deleteParticipant(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @PathVariable Long postId
    ) {
        postService.deleteParticipant(loginUser.getUserId(), postId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("참여 신청이 취소되었습니다.", null));
    }

    //endregion
    //endregion
    //region 게시글좋아요
    // 게시글 좋아요 생성
    @PostMapping("/posts/{id}/likes")
    @Operation(summary = "게시글 좋아요 추가", description = "게시글에 좋아요를 추가한다", tags = {"PostLike"})
    public ResponseEntity<ApiResponse<PostLikeResponse>> addLike(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PostLikeResponse dto = postService.addLike(id, userDetails.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("게시글 좋아요가 추가되었습니다.", dto));
    }

    // 게시글 좋아요 삭제
    @DeleteMapping("/posts/{id}/likes")
    @Operation(summary = "게시글 좋아요 삭제", description = "게시글에 좋아요를 취소한다", tags = {"PostLike"})
    public ResponseEntity<ApiResponse<Void>> removeLike(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        postService.removeLike(id, userDetails.getUserId());

        return ResponseEntity.ok(ApiResponse.ok("게시글 좋아요가 삭제되었습니다.", null));
    }

    //endregion
    //region 게시글초대
    //region 게시글초대 조회 제외메서드
    // 게시글 초대 생성
    @PostMapping("/posts/{postId}/users/{userId}/invite")
    @Operation(summary = "게시글 초대", description = "본인 게시글에 상대방을 초대한다", tags = {"Invite"})
    public ResponseEntity<ApiResponse<InviteResponse>> inviteUser(
            @PathVariable Long postId,
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long loginUserId = loginUser.getUserId();
        InviteResponse responseDto = postService.createInvite(postId, userId, loginUserId);
        URI location = URI.create("/api/posts/" + postId);
        return ResponseEntity.created(location).body(ApiResponse.ok("초대가 완료되었습니다.", responseDto));
    }

    // 게시글 초대 취소
    @DeleteMapping("/posts/{postId}/users/{userId}/invite")
    @Operation(summary = "게시글 초대 취소", description = "본인 게시글에 초대한 것을 취소한다", tags = {"Invite"})
    public ResponseEntity<ApiResponse<Void>> deleteInvite(
            @PathVariable Long postId,
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long loginUserId = loginUser.getUserId();

        postService.deleteInvite(postId, userId, loginUserId);
        return ResponseEntity.ok(ApiResponse.ok("초대가 취소되었습니다.", null));
    }


    //초대 수락
    @PostMapping("/post/{postId}/invite/accept")
    @Operation(summary = "받은 초대 수락", description = "본인이 받은 초대를 수락한다", tags = {"Invite"})
    public ResponseEntity<ApiResponse<Void>> acceptForInvite(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long loginUserId = loginUser.getUserId();
        postService.acceptForInvite(postId, loginUserId);

        return ResponseEntity.ok(ApiResponse.ok("초대를 수락하였습니다.", null));
    }

    //초대 거절
    @PostMapping("/post/{postId}/invite/reject")
    @Operation(summary = "받은 초대 거절", description = "본인이 받은 초대를 거절한다", tags = {"Invite"})
    public ResponseEntity<ApiResponse<Void>> rejectForInvite(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long loginUserId = loginUser.getUserId();
        postService.rejectForInvite(postId, loginUserId);

        return ResponseEntity.ok(ApiResponse.ok("초대를 거절하였습니다.", null));
    }

    //endregion


    // ----------------------------------이하 ES 메서드----------------------------------------

    //게시물 단건 조회
    @GetMapping("/posts/{id}")
    @Operation(summary = "[ES] 게시글 단건 조회", description = "게시글 하나를 조회한다", tags = {"Post"})
    public ResponseEntity<ApiResponse<PostResponse>> getPostByIdES(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("게시글이 조회되었습니다.", postService.findPostES(id)));
    }

    // 내가 작성한 게시글 조회
    @GetMapping("/my/written-posts")
    @Operation(summary = "[ES] 작성한 게시글 조회", description = "본인이 작성한 게시글 목록을 조회한다", tags = {"Post"})
    public ResponseEntity<ApiResponse<PagedResponse<PostResponse>>> getMyWrittenPostsES(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        Long id = userDetails.getUserId();
        PagedResponse<PostResponse> myLikedPost = postService.findMyWrittenPostsES(id, pageable);
        return ResponseEntity.ok(ApiResponse.ok("내가 작성 한 게시물이 조회되었습니다.", myLikedPost));
    }


    //추천 게시물 조회

    // 내 좋아요 게시글 목록 조회
    @GetMapping("/my/likes")
    @Operation(summary = "[ES] 좋아요 게시글 조회", description = "본인이 좋아요한 게시글 목록을 조회한다", tags = {"Post"})
    public ResponseEntity<ApiResponse<PagedResponse<PostResponse>>> getMyLikedPostsES(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        Long id = userDetails.getUserId();
        PagedResponse<PostResponse> myLikedPost = postService.findMyLikedPostsES(id, pageable);
        return ResponseEntity.ok(ApiResponse.ok("내 좋아요 게시글 목록이 조회되었습니다.", myLikedPost));
    }

    //내 신청/성사된 게시글 조회
    @GetMapping("/my/confirmed-posts")
    @Operation(summary = "[ES] 신청/성사된 게시글 조회", description = "본인이 신청한/성사된 게시글 목록을 조회한다", tags = {"Post"})
    public ResponseEntity<ApiResponse<PagedResponse<PostResponse>>> getMyConfirmedPostsES(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) JoinStatus joinStatus,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        Long id = userDetails.getUserId();
        PagedResponse<PostResponse> myLikedPost = postService.findMyConfirmedPostsES(id, joinStatus, pageable);
        return ResponseEntity.ok(ApiResponse.ok("참여 신청한 게시글 목록이 조회되었습니다.", myLikedPost));
    }

    // 게시물 조건 조회
    @GetMapping("/posts")
    @Operation(summary = "[ES] 게시글 검색 조회", description = "게시글 목록을 검색하거나, 전체 조회한다", tags = {"Post"})
    public ResponseEntity<ApiResponse<PagedResponse<PostResponse>>> searchPostsES(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "1") Integer maxParticipants,
            @RequestParam(required = false) AgeLimit ageLimit,
            @RequestParam(required = false) JobCategory jobCategoryLimit,
            @RequestParam(required = false) Gender genderLimit
    ) {
        Pageable pageable = PageRequest.of(page, size);

        PagedResponse<PostResponse> pagedResponse = postService.getPostDtosBySearchES(
                pageable, maxParticipants, ageLimit, jobCategoryLimit, genderLimit);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("게시글 목록이 조회되었습니다.", pagedResponse));
    }

    // 신청자 목록 조회
    @GetMapping("/posts/{postId}/participant")
    @Operation(summary = "[ES] 게시글 신청자 목록 조회", description = "본인 게시글에 신청한 사용자 목록을 조회한다", tags = {"Participate"})
    public ResponseEntity<ApiResponse<PagedResponse<ParticipantResponse>>> getPendingParticipantListES(
            @AuthenticationPrincipal CustomUserDetails author,
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PagedResponse<ParticipantResponse> response = postService.getParticipantListByPendingES(author.getUserId(), postId, page, size);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("신청자 목록이 조회되었습니다.", response));
    }

    // 참가자 목록 조회
    @GetMapping("/posts/{postId}/participate/confirmed")
    @Operation(summary = "[ES] 게시글 참가자 목록 조회", description = "본인 게시글에 참가한 사용자 목록을 조회한다", tags = {"Participate"})
    public ResponseEntity<ApiResponse<PagedResponse<ParticipantResponse>>> getAcceptedParticipantListES(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PagedResponse<ParticipantResponse> response = postService.getParticipantListByAcceptedES(loginUser.getUserId(), postId, page, size);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("참여자 목록이 조회되었습니다.", response));
    }

    //내 초대받은 목록 조회
    @GetMapping("/my/invited")
    @Operation(summary = "[ES] 초대받은 목록 조회", description = "본인이 초대받은 목록을 조회한다", tags = {"Invite"})
    public ResponseEntity<ApiResponse<PagedResponse<InviteResponse>>> getMyInvitedES(
            @AuthenticationPrincipal CustomUserDetails loggedInUser,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long userId = loggedInUser.getUserId();
        PagedResponse<InviteResponse> inviteResponseDto = postService.getMyInvitedES(userId, pageable);

        return ResponseEntity.ok(ApiResponse.ok("초대 받은 목록 조회가 완료되었습니다", inviteResponseDto));
    }

    //내가 보낸 초대 목록 조회
    @GetMapping("/my/invite")
    @Operation(summary = "[ES] 초대보낸 목록 조회", description = "본인이 초대한 목록을 조회한다", tags = {"Invite"})
    public ResponseEntity<ApiResponse<PagedResponse<InviteResponse>>> getMyInviteES(
            @AuthenticationPrincipal CustomUserDetails loggedInUser,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long userId = loggedInUser.getUserId();
        PagedResponse<InviteResponse> inviteResponseDto = postService.getMyInviteES(userId, pageable);

        return ResponseEntity.ok(ApiResponse.ok("초대 보낸 목록 조회가 완료되었습니다", inviteResponseDto));
    }

}
