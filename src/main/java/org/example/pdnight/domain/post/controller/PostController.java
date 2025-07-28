package org.example.pdnight.domain.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.post.dto.request.PostRequestDto;
import org.example.pdnight.domain.post.dto.request.PostStatusRequestDto;
import org.example.pdnight.domain.post.dto.request.PostUpdateRequestDto;
import org.example.pdnight.domain.post.dto.response.PostCreateAndUpdateResponseDto;
import org.example.pdnight.domain.post.dto.response.PostResponseDto;
import org.example.pdnight.domain.post.dto.response.PostResponseWithApplyStatusDto;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.service.PostService;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostCreateAndUpdateResponseDto>> savePost(
            @Valid @RequestBody PostRequestDto request,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long userId = loginUser.getUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("정상적으로 등록되었습니다.", postService.createPost(userId, request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponseWithApplyStatusDto>> getPostById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("게시글이 조회되었습니다.", postService.findOpenedPost(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long userId = loginUser.getUserId();
        postService.deletePostById(userId, id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("게시글이 삭제되었습니다.", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<PostResponseWithApplyStatusDto>>> searchPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "1") Integer maxParticipants,
            @RequestParam(required = false) AgeLimit ageLimit,
            @RequestParam(required = false) JobCategory jobCategoryLimit,
            @RequestParam(required = false) Gender genderLimit,
            @RequestParam(required = false) List<Long> hobbyIdList,
            @RequestParam(required = false) List<Long> techStackIdList
    ) {
        Pageable pageable = PageRequest.of(page, size);

        PagedResponse<PostResponseWithApplyStatusDto> pagedResponse = postService.getPostDtosBySearch(
                pageable, maxParticipants, ageLimit, jobCategoryLimit, genderLimit,
                hobbyIdList, techStackIdList
        );
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("게시글 목록이 조회되었습니다.", pagedResponse));
    }

    @PatchMapping("/{id}")
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

    @PatchMapping("/{id}/status")
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
    @GetMapping("/suggestedPosts")
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

}
