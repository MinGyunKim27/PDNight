package org.example.pdnight.domain.post.controller;

import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.post.dto.request.PostRequestDto;
import org.example.pdnight.domain.post.dto.response.PostResponseDto;
import org.example.pdnight.domain.post.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
	public ResponseEntity<ApiResponse<PostResponseDto>> getPostById(@RequestParam Long id) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.ok("게시글이 조회되었습니다.", postService.findOpenedPost(id)));
	}



}
