package org.example.pdnight.domain.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.common.enums.JoinStatus;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.post.dto.request.PostRequestDto;
import org.example.pdnight.domain.post.dto.request.PostUpdateRequestDto;
import org.example.pdnight.domain.post.dto.response.PostResponseDto;
import org.example.pdnight.domain.post.dto.response.PostResponseWithApplyStatusDto;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.post.repository.PostRepository;
import org.example.pdnight.domain.post.repository.PostRepositoryQuery;
import org.example.pdnight.domain.user.dto.response.PostWithJoinStatusAndAppliedAtResponseDto;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
	@Mock
	private PostRepository postRepository;


	@Mock
	private PostRepositoryQuery postRepositoryQuery;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private PostService postService;

	private User mockUser;
	private Post post;
	private PostRequestDto postRequestDto;
	private PostUpdateRequestDto postUpdateRequestDto;

	//테스트 코드 사전 세팅
	@BeforeEach
	void setUp() {
		//User 목 객체 세팅
		mockUser = Mockito.mock(User.class);
		lenient().when(mockUser.getId()).thenReturn(1L);

		//요청 DTO 세팅
		postRequestDto = PostRequestDto.builder()
			.title("제목")
			.timeSlot(LocalDateTime.now())
			.publicContent("공개 내용")
			.privateContent("비공개 내용")
			.maxParticipants(4)
			.build();

		// 테스트 용 post 객체
		post = Post.createPost(
			mockUser,
			postRequestDto.getTitle(),
			postRequestDto.getTimeSlot(),
			postRequestDto.getPublicContent(),
			postRequestDto.getPrivateContent(),
			postRequestDto.getMaxParticipants(),
			postRequestDto.getGenderLimit(),
			postRequestDto.getJobCategoryLimit(),
			postRequestDto.getAgeLimit()
		);
	}

	@Test
	@DisplayName("게시글 등록 테스트")
	void createPost_게시글_등록_테스트() {
		//given
		Long userId = 1L;

		//when
		when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(mockUser));
		when(postRepository.save(any(Post.class))).thenReturn(post);
		PostResponseDto responseDto = postService.createPost(userId, postRequestDto);

		//then
		assertNotNull(responseDto);
		assertEquals(post.getTitle(), responseDto.getTitle());
	}

	@Test
	@DisplayName("게시글 단건 조회 테스트")
	void findOpenedPost_게시글_단건_조회_테스트() {
		//given
		Long postId = 1L;

		//when
		when(postRepository.findByIdAndStatus(postId, PostStatus.OPEN)).thenReturn(Optional.of(post));
		PostResponseWithApplyStatusDto responseDto = postService.findOpenedPost(postId);

		//then
		assertNotNull(responseDto);
		assertEquals(post.getTitle(), responseDto.getTitle());
	}

	@Test
	@DisplayName("내가 작성한 게시글 삭제 시 예외 테스트")
	void deletePostById_다른_작성자_게시글_삭제() {
		//given
		Long postId = 1L;
		//작성자와 다른 Id 일때
		Long userId = 5L;

		when(postRepository.findById(postId)).thenReturn(Optional.of(post));

		//when, then
		BaseException exception = assertThrows(BaseException.class, () -> {
			postService.deletePostById(userId, postId);
		});

		assertEquals(ErrorCode.POST_FORBIDDEN.getMessage(), exception.getMessage());

	}

	@Test
	@DisplayName("게시글 수정 테스트")
	void updatePostDetails_게시글_수정_테스트() {
		//given
		Long userId = 1L;
		Long postId = 1L;

		String oldTitle = post.getTitle();
		JobCategory oldJobCategoryLimit = post.getJobCategoryLimit();

		postUpdateRequestDto = PostUpdateRequestDto.builder()
			.title("수정된 제목")
			.timeSlot(LocalDateTime.now())
			.publicContent("수정된 공개 내용")
			.privateContent("수정된 비공개 내용")
			.maxParticipants(5)
			.genderLimit(Gender.FEMALE)
			.jobCategoryLimit(JobCategory.BACK_END_DEVELOPER)
			.ageLimit(AgeLimit.AGE_30S)
			.build();

		when(postRepository.findById(postId)).thenReturn(Optional.of(post));

		//when
		PostResponseDto responseDto = postService.updatePostDetails(userId, postId, postUpdateRequestDto);

		//then
		assertNotNull(responseDto);

		//이전 내용과 같으면 안됨
		assertNotEquals(responseDto.getTitle(), oldTitle);
		assertNotEquals(responseDto.getJobCategoryLimit(), oldJobCategoryLimit);

		//변경 내용 확인
		assertEquals(responseDto.getTitle(), post.getTitle());
		assertEquals(responseDto.getJobCategoryLimit(), post.getJobCategoryLimit());
	}

	@Test
	@DisplayName("게시글 일부 수정 테스트")
	void updatePostDetails_게시글_일부_수정_테스트() {
		//given
		Long userId = 1L;
		Long postId = 1L;

		String oldTitle = post.getTitle();
		String oldPublicContent = post.getPublicContent();

		postUpdateRequestDto = PostUpdateRequestDto.builder()
			.title(null)
			.publicContent("수정된 공개 내용")
			.build();

		when(postRepository.findById(postId)).thenReturn(Optional.of(post));

		//when
		PostResponseDto responseDto = postService.updatePostDetails(userId, postId, postUpdateRequestDto);

		//then
		assertNotNull(responseDto);

		//null은 변경 안돼야 함
		assertEquals(responseDto.getTitle(), oldTitle);
		//세팅이 안된 필드도 변경 안돼야 함
		assertEquals(responseDto.getPrivateContent(), post.getPrivateContent());

		//수정 필드는 변경돼야 함
		assertNotEquals(responseDto.getPublicContent(), oldPublicContent);
		assertEquals(responseDto.getPublicContent(), post.getPublicContent());
	}

	@Test
	@DisplayName("조건 검색 정상 동작 테스트")
	void getPostDtosBySearch_조건_맞춰서_정상_반환_테스트() {
		//given
		Pageable pageable = PageRequest.of(0, 10);
		Integer maxParticipants = 4;
		AgeLimit ageLimit = AgeLimit.AGE_30S;
		JobCategory jobCategoryLimit = JobCategory.BACK_END_DEVELOPER;
		Gender genderLimit = Gender.MALE;

		PostResponseWithApplyStatusDto mockDto = new PostResponseWithApplyStatusDto();

		Page<PostResponseWithApplyStatusDto> page = new PageImpl<>(List.of(mockDto), pageable, 1);

		when(postRepositoryQuery.findPostDtosBySearch(
			pageable,
			maxParticipants,
			ageLimit,
			jobCategoryLimit,
			genderLimit)
		).thenReturn(page);

		//when
		Page<PostResponseWithApplyStatusDto> responseDtos = postService.getPostDtosBySearch(pageable, maxParticipants, ageLimit,
			jobCategoryLimit, genderLimit);

		//then
		assertEquals(1, responseDtos.getTotalElements());
		assertEquals(mockDto, responseDtos.getContent().get(0));
	}

	@Test
	@DisplayName("조건에 맞는 조회 결과 없을 때 빈페이지 반환")
	void getPostDtosBySearch_조건_맞는_결과_없음_테스트() {
		//given
		Pageable pageable = PageRequest.of(0, 10);
		Integer maxParticipants = 4;
		AgeLimit ageLimit = AgeLimit.AGE_30S;
		JobCategory jobCategoryLimit = JobCategory.BACK_END_DEVELOPER;
		Gender genderLimit = Gender.MALE;

		Page<PostResponseWithApplyStatusDto> emptyPage = new PageImpl<>(List.of(), pageable, 0);

		when(postRepositoryQuery.findPostDtosBySearch(
			pageable,
			maxParticipants,
			ageLimit,
			jobCategoryLimit,
			genderLimit)
		).thenReturn(emptyPage);

		//when
		Page<PostResponseWithApplyStatusDto> responseDtos = postService.getPostDtosBySearch(pageable, maxParticipants, ageLimit,
			jobCategoryLimit, genderLimit);

		//then
		assertEquals(0, responseDtos.getTotalElements());
		assertTrue(responseDtos.getContent().isEmpty());
	}

	@Test
	void findMyLikedPosts_정상조회() {
		// given
		Long userId = 1L;
		Pageable pageable = PageRequest.of(0, 10);

		PostResponseWithApplyStatusDto post1 = new PostResponseWithApplyStatusDto(
				1L,
				2L,
				"제목1",
				LocalDateTime.now(),
				"공개내용1",
				"비공개내용1",
				PostStatus.OPEN,
				4,
				Gender.MALE,
				JobCategory.BACK_END_DEVELOPER,
				AgeLimit.AGE_20S,
				2L, // 신청자 수
				1L, // 확정자 수
				LocalDateTime.now(),
				LocalDateTime.now()
		);

		PostResponseWithApplyStatusDto post2 = new PostResponseWithApplyStatusDto(
				2L,
				2L,
				"제목2",
				LocalDateTime.now(),
				"공개내용2",
				"비공개내용2",
				PostStatus.OPEN,
				4,
				Gender.MALE,
				JobCategory.BACK_END_DEVELOPER,
				AgeLimit.AGE_20S,
				3L,
				2L,
				LocalDateTime.now(),
				LocalDateTime.now()
		);

		List<PostResponseWithApplyStatusDto> postList = List.of(post1, post2);
		Page<PostResponseWithApplyStatusDto> postPage = new PageImpl<>(postList);

		when(postRepositoryQuery.getMyLikePost(userId, pageable)).thenReturn(postPage);

		// when
		PagedResponse<PostResponseWithApplyStatusDto> response = postService.findMyLikedPosts(userId, pageable);

		// then
		assertThat(response.contents()).hasSize(2);
		verify(postRepositoryQuery).getMyLikePost(userId, pageable);
	}



	@Test
	void findMyConfirmedPosts_정상조회() {
		// given
		Long userId = 1L;
		JoinStatus joinStatus = JoinStatus.ACCEPTED;
		Pageable pageable = PageRequest.of(0, 10);

		List<PostWithJoinStatusAndAppliedAtResponseDto> dtoList =
				List.of(new PostWithJoinStatusAndAppliedAtResponseDto(), new PostWithJoinStatusAndAppliedAtResponseDto());
		Page<PostWithJoinStatusAndAppliedAtResponseDto> page = new PageImpl<>(dtoList);

		when(postRepositoryQuery.getConfirmedPost(userId, joinStatus, pageable)).thenReturn(page);

		// when
		PagedResponse<PostWithJoinStatusAndAppliedAtResponseDto> response =
				postService.findMyConfirmedPosts(userId, joinStatus, pageable);

		// then
		assertThat(response.contents()).hasSize(2);
		verify(postRepositoryQuery).getConfirmedPost(userId, joinStatus, pageable);
	}

	@Test
	void findMyWrittenPosts_정상조회() {
		// given
		Long userId = 1L;
		Pageable pageable = PageRequest.of(0, 10);

		List<PostResponseWithApplyStatusDto> dtoList = List.of(new PostResponseWithApplyStatusDto(), new PostResponseWithApplyStatusDto());
		Page<PostResponseWithApplyStatusDto> page = new PageImpl<>(dtoList);

		when(postRepositoryQuery.getWrittenPost(userId, pageable)).thenReturn(page);

		// when
		PagedResponse<PostResponseWithApplyStatusDto> response = postService.findMyWrittenPosts(userId, pageable);

		// then
		assertThat(response.contents()).hasSize(2);
		verify(postRepositoryQuery).getWrittenPost(userId, pageable);
	}

}