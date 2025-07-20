package org.example.pdnight.domain.post.controller;

import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.post.dto.request.PostRequestDto;
import org.example.pdnight.domain.post.dto.response.PostResponseDto;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	@PostMapping
	public ResponseEntity<ApiResponse<PostResponseDto>> savePost(@Valid @RequestBody PostRequestDto request) {
		//인증객체 도입 전 임시 데이터 1번 유저
		Long userId = 1L;
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.ok("정상적으로 등록되었습니다.", postService.createPost(userId, request)));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<PostResponseDto>> getPostById(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.ok("게시글이 조회되었습니다.", postService.findOpenedPost(id)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long id) {
		//인증객체 도입 전 임시 데이터 1번 유저
		Long userId = 1L;
		postService.deletePostById(userId, id);
		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.ok("게시글이 삭제되었습니다.", null));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<Page<PostResponseDto>>> searchPosts(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "1") Integer maxParticipants,
		@RequestParam(required = false) AgeLimit ageLimit,
		@RequestParam(required = false) JobCategory jobCategoryLimit,
		@RequestParam(required = false) Gender genderLimit
	) {
		Pageable pageable = PageRequest.of(page, size);
		Page<PostResponseDto> postDtos = postService.getPostDtosBySearch(pageable, maxParticipants, ageLimit,
			jobCategoryLimit, genderLimit);
		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.ok("게시글 목록이 조회되었습니다.", postDtos));
	}

}
