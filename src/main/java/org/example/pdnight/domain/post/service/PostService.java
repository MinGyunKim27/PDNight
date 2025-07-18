package org.example.pdnight.domain.post.service;

import java.util.Optional;

import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.post.dto.request.PostRequestDto;
import org.example.pdnight.domain.post.dto.response.PostResponseDto;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.repository.PostRepository;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
	private final PostRepository postRepository;
	private final UserRepository userRepository;

	public PostResponseDto createPost(Long userId, PostRequestDto request) {
		//임시 메서드 User 도메인 작업에 따라 변동될 것
		User foundUser = getUserOrThrow(userRepository.findByIdAndIsDeletedFalse(userId));

		Post post = Post.createPost(
			foundUser,
			request.getTitle(),
			request.getTimeslot(),
			request.getPublicContent(),
			request.getPrivateContent(),
			request.getMaxParticipants(),
			request.getGenderLimit(),
			request.getJobCategoryLimit(),
			request.getAgeLimit()
		);

		Post savedPost = postRepository.save(post);
		return new PostResponseDto(savedPost);
	}


	//이하 헬퍼 메서드
	private Post getPostOrThrow(Optional<Post> post) {
		return post.orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND, "id 값과 일치하는 게시글을 찾을 수 없습니다."));
	}

	private User getUserOrThrow(Optional<User> user) {
		return user.orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND, "id 값과 일치하는 사용자를 찾을 수 없습니다."));
	}
}
