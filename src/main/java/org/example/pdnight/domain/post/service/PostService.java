package org.example.pdnight.domain.post.service;

import java.util.Optional;

import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.participant.enums.JoinStatus;
import org.example.pdnight.domain.post.dto.request.PostRequestDto;
import org.example.pdnight.domain.post.dto.request.PostStatusRequestDto;
import org.example.pdnight.domain.post.dto.request.PostUpdateRequestDto;
import org.example.pdnight.domain.post.dto.response.PostResponseDto;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.post.repository.PostRepository;
import org.example.pdnight.domain.post.repository.PostRepositoryQueryImpl;
import org.example.pdnight.domain.user.dto.response.PostWithJoinStatusAndAppliedAtResponseDto;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final PostRepositoryQueryImpl postRepositoryQuery;

	@Transactional
	public PostResponseDto createPost(Long userId, PostRequestDto request) {
		//임시 메서드 User 도메인 작업에 따라 변동될 것
		User foundUser = getUserOrThrow(userRepository.findByIdAndIsDeletedFalse(userId));

		Post post = Post.createPost(
			foundUser,
			request.getTitle(),
			request.getTimeSlot(),
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

	//조회는 상태값 "OPEN" 인 게시글만 가능
	@Transactional(readOnly = true)
	public PostResponseDto findOpenedPost(Long id) {
		Post foundPost = getPostOrThrow(postRepository.findByIdAndStatus(id, PostStatus.OPEN));
		return new PostResponseDto(foundPost);
	}

	@Transactional
	public void deletePostById(Long userId, Long id) {
		Post foundPost = getPostOrThrow(postRepository.findById(id));
		validateAuthor(userId, foundPost);
		postRepository.delete(foundPost);
	}

	@Transactional(readOnly = true)
	public Page<PostResponseDto> getPostDtosBySearch(
		Pageable pageable,
		Integer maxParticipants,
		AgeLimit ageLimit,
		JobCategory jobCategoryLimit,
		Gender genderLimit
	) {
		return postRepository.findPostDtosBySearch(pageable, maxParticipants,
			ageLimit, jobCategoryLimit, genderLimit);
	}

	@Transactional
	public PostResponseDto updatePostDetails(Long userId, Long id, PostUpdateRequestDto request) {
		Post foundPost = getPostOrThrow(postRepository.findById(id));
		validateAuthor(userId, foundPost);

		foundPost.updatePostIfNotNull(
			request.getTitle(),
			request.getTimeSlot(),
			request.getPublicContent(),
			request.getPrivateContent(),
			request.getMaxParticipants(),
			request.getGenderLimit(),
			request.getJobCategoryLimit(),
			request.getAgeLimit()
		);

		return new PostResponseDto(foundPost);
	}

	@Transactional
	public PostResponseDto changeStatus(Long userId, Long id, PostStatusRequestDto request) {
		//상태값 변경은 어떤 상태라도 불러와서 수정
		Post foundPost = getPostOrThrow(postRepository.findById(id));
		validateAuthor(userId, foundPost);

		//변동사항 있을시에만 업데이트
		if(!foundPost.getStatus().equals(request.getStatus())){
			foundPost.updateStatus(request.getStatus());
		}

		return new PostResponseDto(foundPost);
	}

	public PagedResponse<PostResponseDto> findMyLikedPosts(Long userId, Pageable pageable){
		Page<Post> myLikePost = postRepositoryQuery.getMyLikePost(userId, pageable);
		Page<PostResponseDto> postResponseDtos = myLikePost.map(PostResponseDto::toDto);
		return PagedResponse.from(postResponseDtos);
	}

	public PagedResponse<PostWithJoinStatusAndAppliedAtResponseDto> findMyConfirmedPosts(Long userId, JoinStatus joinStatus, Pageable pageable) {
		Page<PostWithJoinStatusAndAppliedAtResponseDto> myLikePost = postRepositoryQuery.getConfirmedPost(userId, joinStatus, pageable);
		return PagedResponse.from(myLikePost);
	}

	public PagedResponse<PostResponseDto> findMyWrittenPosts(Long userId, Pageable pageable) {
		Page<PostResponseDto> myLikePost = postRepositoryQuery.getWrittenPost(userId, pageable);
		return PagedResponse.from(myLikePost);
	}

	//이하 헬퍼 메서드
	private Post getPostOrThrow(Optional<Post> post) {
		return post.orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
	}

	private User getUserOrThrow(Optional<User> user) {
		return user.orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
	}

	private void validateAuthor(Long userId, Post post) {
		if (!post.getAuthor().getId().equals(userId)) {
			throw new BaseException(ErrorCode.POST_FORBIDDEN);
		}
	}

}
