package org.example.pdnight.domain.post.presentation;

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
    // 게시물 생성
    @PostMapping("/posts")
    public ResponseEntity<ApiResponse<PostResponse>> savePost(
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long userId = loginUser.getUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("정상적으로 등록되었습니다.", postService.createPost(userId, request)));
    }

    // 게시물 수정
    @PatchMapping("/posts/{id}")
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

    // 게시물 상태 수정
    @PatchMapping("/posts/{id}/status")
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

    // 게시물 삭제
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long userId = loginUser.getUserId();
        postService.deletePostById(userId, id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("게시글이 삭제되었습니다.", null));
    }

    // 어드민 - 유저게시물 삭제
    @DeleteMapping("/admin/posts/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long id
    ) {
        postService.deleteAdminPostById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("게시글이 삭제되었습니다.", null));
    }

    //endregion
    //region 게시물조회
    //추천 게시물 조회
    @GetMapping("/posts/suggested-posts")
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

    // 내 좋아요 게시글 목록 조회
    @GetMapping("/my/likes")
    public ResponseEntity<ApiResponse<PagedResponse<PostResponse>>> getMyLikedPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        Long id = userDetails.getUserId();
        PagedResponse<PostResponse> myLikedPost = postService.findMyLikedPosts(id, pageable);
        return ResponseEntity.ok(ApiResponse.ok("내 좋아요 게시글 목록이 조회되었습니다.", myLikedPost));
    }

    //내 신청/성사된 게시글 조회
    @GetMapping("/my/confirmed-posts")
    public ResponseEntity<ApiResponse<PagedResponse<PostResponse>>> getMyConfirmedPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) JoinStatus joinStatus,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        Long id = userDetails.getUserId();
        PagedResponse<PostResponse> myLikedPost = postService.findMyConfirmedPosts(id, joinStatus, pageable);
        return ResponseEntity.ok(ApiResponse.ok("참여 신청한 게시글 목록이 조회되었습니다.", myLikedPost));
    }

    //게시물 단건 조회
    @GetMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> getPostById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("게시글이 조회되었습니다.", postService.findPost(id)));
    }

    // 내가 작성한 게시글 조회
    @GetMapping("/my/written-posts")
    public ResponseEntity<ApiResponse<PagedResponse<PostResponse>>> getMyWrittenPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        Long id = userDetails.getUserId();
        PagedResponse<PostResponse> myLikedPost = postService.findMyWrittenPosts(id, pageable);
        return ResponseEntity.ok(ApiResponse.ok("내가 작성 한 게시물이 조회되었습니다.", myLikedPost));
    }

    // 게시물 조건 조회
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<PagedResponse<PostResponse>>> searchPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "1") Integer maxParticipants,
            @RequestParam(required = false) AgeLimit ageLimit,
            @RequestParam(required = false) JobCategory jobCategoryLimit,
            @RequestParam(required = false) Gender genderLimit
    ) {
        Pageable pageable = PageRequest.of(page, size);

        PagedResponse<PostResponse> pagedResponse = postService.getPostDtosBySearch(
                pageable, maxParticipants, ageLimit, jobCategoryLimit, genderLimit);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("게시글 목록이 조회되었습니다.", pagedResponse));
    }

    //endregion
    //endregion
    //region 게시글신청자
    //region 게시글신청자 조회제외 메서드들
    // 게시물 참여 신청
    @PostMapping("/posts/{postId}/participate")
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

    // 게시물 신청 수락 or 거절
    @PatchMapping("/posts/{postId}/participate/users/{userId}")
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

    // 게시물 참여 신청 삭제
    @DeleteMapping("/posts/{postId}/participate")
    public ResponseEntity<ApiResponse<Void>> deleteParticipant(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @PathVariable Long postId
    ) {
        postService.deleteParticipant(loginUser.getUserId(), postId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("참여 신청이 취소되었습니다.", null));
    }

    //endregion
    //region 게시글신청자 조회 메서드
    // 신청자 목록 조회
    @GetMapping("/posts/{postId}/participant")
    public ResponseEntity<ApiResponse<PagedResponse<ParticipantResponse>>> getPendingParticipantList(
            @AuthenticationPrincipal CustomUserDetails author,
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PagedResponse<ParticipantResponse> response = postService.getParticipantListByPending(author.getUserId(), postId, page, size);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("신청자 목록이 조회되었습니다.", response));
    }

    // 참가자 목록 조회
    @GetMapping("/posts/{postId}/participate/confirmed")
    public ResponseEntity<ApiResponse<PagedResponse<ParticipantResponse>>> getAcceptedParticipantList(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PagedResponse<ParticipantResponse> response = postService.getParticipantListByAccepted(loginUser.getUserId(), postId, page, size);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("참여자 목록이 조회되었습니다.", response));
    }

    //endregion
    //endregion
    //region 게시물좋아요
    // 게시물 좋아요 생성
    @PostMapping("/posts/{id}/likes")
    public ResponseEntity<ApiResponse<PostLikeResponse>> addLike(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PostLikeResponse dto = postService.addLike(id, userDetails.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("게시글 좋아요가 추가되었습니다.", dto));
    }

    // 게시물 좋아요 삭제
    @DeleteMapping("/posts/{id}/likes")
    public ResponseEntity<ApiResponse<Void>> removeLike(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        postService.removeLike(id, userDetails.getUserId());

        return ResponseEntity.ok(ApiResponse.ok("게시글 좋아요가 삭제되었습니다.", null));
    }

    //endregion
    //region 게시물초대
    //region 게시물초대 조회 제외메서드
    // 게시물초대 생성
    @PostMapping("/posts/{postId}/users/{userId}/invite")
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

    // 게시물 초대 취소
    @DeleteMapping("/posts/{postId}/users/{userId}/invite")
    public ResponseEntity<ApiResponse<Void>> deleteInvite(
            @PathVariable Long postId,
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long loginUserId = loginUser.getUserId();

        postService.deleteInvite(postId, userId, loginUserId);
        return ResponseEntity.ok(ApiResponse.ok("초대가 취소되었습니다.", null));
    }

    //endregion
    //region 게시물초대 조회 메서드
    //내 초대받은 목록 조회
    @GetMapping("/my/invited")
    public ResponseEntity<ApiResponse<PagedResponse<InviteResponse>>> getMyInvited(
            @AuthenticationPrincipal CustomUserDetails loggedInUser,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long userId = loggedInUser.getUserId();
        PagedResponse<InviteResponse> inviteResponseDto = postService.getMyInvited(userId, pageable);

        return ResponseEntity.ok(ApiResponse.ok("초대 받은 목록 조회가 완료되었습니다", inviteResponseDto));
    }

    //내가 보낸 초대 목록 조회
    @GetMapping("/my/invite")
    public ResponseEntity<ApiResponse<PagedResponse<InviteResponse>>> getMyInvite(
            @AuthenticationPrincipal CustomUserDetails loggedInUser,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long userId = loggedInUser.getUserId();
        PagedResponse<InviteResponse> inviteResponseDto = postService.getMyInvite(userId, pageable);

        return ResponseEntity.ok(ApiResponse.ok("초대 보낸 목록 조회가 완료되었습니다", inviteResponseDto));
    }
    //endregion

    @PostMapping("/post/{postId}/invite/accept")
    public ResponseEntity<ApiResponse<Void>> acceptForInvite(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long loginUserId = loginUser.getUserId();
        postService.acceptForInvite(postId, loginUserId);

        return ResponseEntity.ok(ApiResponse.ok("초대를 수락하였습니다.", null));
    }

    @PostMapping("/post/{postId}/invite/reject")
    public ResponseEntity<ApiResponse<Void>> rejectForInvite(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long loginUserId = loginUser.getUserId();
        postService.rejectForInvite(postId, loginUserId);

        return ResponseEntity.ok(ApiResponse.ok("초대를 거절하였습니다.", null));
    }

    //endregion
}