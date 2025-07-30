package org.example.pdnight.domain.post.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.example.pdnight.domain.post.application.PostUseCase.PostService;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.JobCategory;
import org.example.pdnight.domain.post.presentation.dto.request.PostRequestDto;
import org.example.pdnight.domain.post.presentation.dto.request.PostStatusRequestDto;
import org.example.pdnight.domain.post.presentation.dto.request.PostUpdateRequestDto;
import org.example.pdnight.domain.post.presentation.dto.response.PostCreateAndUpdateResponseDto;
import org.example.pdnight.domain.post.presentation.dto.response.PostResponseDto;
import org.example.pdnight.domain.post.presentation.dto.response.PostResponseWithApplyStatusDto;
import org.example.pdnight.domain1.common.dto.ApiResponse;
import org.example.pdnight.domain1.common.dto.PagedResponse;
import org.example.pdnight.domain1.participant.dto.response.ParticipantResponse;
import org.example.pdnight.domain1.postLike.dto.response.PostLikeResponse;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/api/posts")
    public ResponseEntity<ApiResponse<PostCreateAndUpdateResponseDto>> savePost(
            @Valid @RequestBody PostRequestDto request,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long userId = loginUser.getUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("정상적으로 등록되었습니다.", postService.createPost(userId, request)));
    }

    @GetMapping("/api/posts/{id}")
    public ResponseEntity<ApiResponse<PostResponseWithApplyStatusDto>> getPostById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("게시글이 조회되었습니다.", postService.findOpenedPost(id)));
    }

    @DeleteMapping("/api/posts/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long userId = loginUser.getUserId();
        postService.deletePostById(userId, id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("게시글이 삭제되었습니다.", null));
    }

    @GetMapping("/api/posts")
    public ResponseEntity<ApiResponse<PagedResponse<PostResponseWithApplyStatusDto>>> searchPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "1") Integer maxParticipants,
            @RequestParam(required = false) AgeLimit ageLimit,
            @RequestParam(required = false) JobCategory jobCategoryLimit,
            @RequestParam(required = false) Gender genderLimit
    ) {
        Pageable pageable = PageRequest.of(page, size);

        PagedResponse<PostResponseWithApplyStatusDto> pagedResponse = postService.getPostDtosBySearch(
                pageable, maxParticipants, ageLimit, jobCategoryLimit, genderLimit );
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("게시글 목록이 조회되었습니다.", pagedResponse));
    }

    @PatchMapping("/api/posts/{id}")
    public ResponseEntity<ApiResponse<PostCreateAndUpdateResponseDto>> updatePost(
            @PathVariable Long id,
            @RequestBody PostUpdateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long userId = loginUser.getUserId();
        PostCreateAndUpdateResponseDto updatedPost = postService.updatePostDetails(userId, id, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("게시글이 수정되었습니다.", updatedPost));
    }

    @PatchMapping("/api/posts/{id}/status")
    public ResponseEntity<ApiResponse<PostResponseDto>> updateStatus(
            @PathVariable Long id,
            @RequestBody PostStatusRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long userId = loginUser.getUserId();
        PostResponseDto updatedPost = postService.changeStatus(userId, id, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("게시글 상태가 수정되었습니다.", updatedPost));
    }


    //추천 게시물 조회
    @GetMapping("/api/posts/suggestedPosts")
    public ResponseEntity<ApiResponse<PagedResponse<PostResponseWithApplyStatusDto>>> suggestedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Long userId = loginUser.getUserId();
        PagedResponse<PostResponseWithApplyStatusDto> pagedResponse = postService.getSuggestedPosts(userId, pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("게시글 목록이 조회되었습니다.", pagedResponse));
    }

    //   ------ admin ------
    @DeleteMapping("/api/admin/posts/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long id
    ) {
        postService.deleteAdminPostById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("게시글이 삭제되었습니다.", null));
    }


    //------------ Participate Controller---------------
    @PostMapping("/api/posts/{postId}/participate")
    public ResponseEntity<ApiResponse<ParticipantResponse>> applyParticipant(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @PathVariable Long postId
    ) {
        ParticipantResponse response = postService.applyParticipant(loginUser.getUserId(), postId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("참여 신청되었습니다.", response));
    }

    @DeleteMapping("/api/posts/{postId}/participate")
    public ResponseEntity<ApiResponse<Void>> deleteParticipant(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @PathVariable Long postId
    ) {
        postService.deleteParticipant(loginUser.getUserId(), postId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("참여 신청이 취소되었습니다.", null));
    }

    @PatchMapping("/api/posts/{postId}/participate/users/{userId}")
    public ResponseEntity<ApiResponse<ParticipantResponse>> changeStatusParticipant(
            @AuthenticationPrincipal CustomUserDetails author,
            @PathVariable Long postId,
            @PathVariable Long userId,
            @RequestParam String status
    ) {
        ParticipantResponse response = postService.changeStatusParticipant(author.getUserId(), userId, postId, status);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("신청자가 수락 혹은 거절되었습니다.", response));
    }

    @GetMapping("/api/posts/{postId}/participant")
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

    @GetMapping("/api/posts/{postId}/participate/confirmed")
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


    //------------- PostLikes Controller ------------
    @PostMapping("/api/posts/{id}/likes")
    public ResponseEntity<ApiResponse<PostLikeResponse>> addLike(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PostLikeResponse dto = postService.addLike(id, userDetails.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("게시글 좋아요가 추가되었습니다.", dto));
    }

    @DeleteMapping("/api/posts/{id}/likes")
    public ResponseEntity<ApiResponse<Void>> removeLike(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        postService.removeLike(id, userDetails.getUserId());

        return ResponseEntity.ok(ApiResponse.ok("게시글 좋아요가 삭제되었습니다.", null));
    }

}
