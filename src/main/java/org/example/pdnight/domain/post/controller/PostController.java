package org.example.pdnight.domain.post.controller;

import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.post.dto.request.PostRequestDto;
import org.example.pdnight.domain.post.dto.response.PostResponseDto;
import org.example.pdnight.domain.post.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	// @PostMapping
	// public ResponseEntity<ApiResponse<PostResponseDto>> savePost(
	// 	@RequestBody PostRequestDto requestDto;
	// ) {
	// 	//인증객체 도입 전 임시 데이터 1번 유저
	// 	Long userId = 1L;
	//
	// 	return;
	// }
}
