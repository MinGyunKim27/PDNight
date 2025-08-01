package org.example.pdnight.domain.post.application.PostUseCase;

import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.domain.post.PostParticipant;
import org.example.pdnight.domain.post.domain.post.PostReader;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.domain.post.presentation.dto.response.ParticipantResponse;
import org.example.pdnight.domain.post.presentation.dto.response.PostResponse;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.example.pdnight.global.common.enums.JobCategory;
import org.example.pdnight.global.common.exception.BaseException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.pdnight.global.common.enums.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostReaderServiceTest {
    @InjectMocks
    private PostReaderService postReaderService;

    @Mock
    private PostReader postReader;

    //region 게시글
    @Test
    @DisplayName("게시글 단건 조회 정상")
    void findPost_게시글_단건_조회_성공() {
        // given
        Long postId = 1L;
        Post post = getParticipants(2, 1);

        when(postReader.getPostById(postId)).thenReturn(Optional.of(post));

        // when
        PostResponse response = postReaderService.findPost(postId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAcceptedParticipantsCount()).isEqualTo(1);
        assertThat(response.getParticipantsCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("게시글 단건 조회 예외 - 게시글이 없는 경우")
    void findPost_게시글_단건_조회_예외() {
        // given
        when(postReader.getPostById(anyLong())).thenReturn(Optional.empty());

        // when & then
        BaseException exception = assertThrows(BaseException.class, () -> {
            postReaderService.findPost(1L);
        });

        assertEquals(POST_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("조건 검색 정상 동작 테스트")
    void getPostDtosBySearch_조건_검색_테스트_성공() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        Integer maxParticipants = 4;
        AgeLimit ageLimit = AgeLimit.AGE_30S;
        JobCategory jobCategoryLimit = JobCategory.BACK_END_DEVELOPER;
        Gender genderLimit = Gender.MALE;


        Post post = getParticipants(2, 1);

        Page<Post> page = new PageImpl<>(List.of(post), pageable, 1);

        when(postReader.findPostsBySearch(
                pageable,
                maxParticipants,
                ageLimit,
                jobCategoryLimit,
                genderLimit)
        ).thenReturn(page);

        //when
        PagedResponse<PostResponse> responseDtos = postReaderService.getPostDtosBySearch(
                pageable,
                maxParticipants,
                ageLimit,
                jobCategoryLimit,
                genderLimit
        );

        //then
        assertThat(responseDtos.contents()).hasSize(1);
        assertEquals(post.getPostParticipants().size(), responseDtos.contents().get(0).getParticipantsCount());
    }

    @Test
    @DisplayName("내가 신청한 게시물 조회 성공")
    void findMyConfirmedPosts_내가_신청한_게시물_조회_성공() {
        // given
        Long userId = 1L;
        JoinStatus joinStatus = JoinStatus.ACCEPTED;
        Pageable pageable = PageRequest.of(0, 10);
        PostResponse response = mock(PostResponse.class);
        Page<PostResponse> page = new PageImpl<>(List.of(response), pageable, 1);

        when(postReader.getConfirmedPost(userId, joinStatus, pageable)).thenReturn(page);

        // when
        PagedResponse<PostResponse> result = postReaderService.findMyConfirmedPosts(userId, joinStatus, pageable);

        // then
        assertThat(result.contents()).hasSize(1);
        assertThat(result.contents().get(0)).isEqualTo(response);
    }

    @Test
    @DisplayName("내가 작성한 게시물 조회 성공")
    void findMyWrittenPosts_내가_작성한_게시물_조회_성공() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Post post = getParticipants(3, 2);
        Page<Post> page = new PageImpl<>(List.of(post), pageable, 1);

        when(postReader.getWrittenPost(userId, pageable)).thenReturn(page);

        // when
        PagedResponse<PostResponse> result = postReaderService.findMyWrittenPosts(userId, pageable);
        PostResponse dto = result.contents().get(0);

        // then
        assertThat(result.contents()).hasSize(1);
        assertThat(dto.getAcceptedParticipantsCount()).isEqualTo(2);
        assertThat(dto.getParticipantsCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("추천 게시물 조회 성공")
    void getSuggestedPosts_추천_게시물_조회_성공() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Post post = getParticipants(4, 1);
        Page<Post> page = new PageImpl<>(List.of(post), pageable, 1);

        when(postReader.getSuggestedPost(userId, pageable)).thenReturn(page);

        // when
        PagedResponse<PostResponse> result = postReaderService.getSuggestedPosts(userId, pageable);
        PostResponse dto = result.contents().get(0);

        // then
        assertThat(result.contents()).hasSize(1);
        assertThat(dto.getAcceptedParticipantsCount()).isEqualTo(1);
        assertThat(dto.getParticipantsCount()).isEqualTo(4);
    }

    @Test
    @DisplayName("좋아요 누른 게시물 조회 성공")
    void findMyLikedPosts_좋아요_누른_게시물_조회_성공() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        PostResponse response = mock(PostResponse.class);
        Page<PostResponse> page = new PageImpl<>(List.of(response), pageable, 1);

        when(postReader.getMyLikePost(userId, pageable)).thenReturn(page);

        // when
        PagedResponse<PostResponse> result = postReaderService.findMyLikedPosts(userId, pageable);

        // then
        assertThat(result.contents()).hasSize(1);
        assertThat(result.contents().get(0)).isEqualTo(response);
    }

    //endregion
    //region 게시물 신청자 리스트
    @Test
    @DisplayName("게시글 신청자 목록 조회 성공")
    void getParticipantListByPending_게시글_신청자_목록_조회_성공() {
        // given
        Long postId = 1L;
        Long loginId = 1L;
        Post post = getParticipants(4, 1);

        when(postReader.getPostById(postId)).thenReturn(Optional.of(post));

        // when
        PagedResponse<ParticipantResponse> result = postReaderService.getParticipantListByPending(loginId, postId, 0, 10);

        // then
        assertThat(result.contents()).hasSize(3);
    }

    @Test
    @DisplayName("게시글 신청자 목록 조회 예외 - 게시글 소유자 아님")
    void getParticipantListByAccepted_게시글_참가자_목록_조회_예외() {
        // given
        Long postId = 1L;
        Long loginId = 2L;
        Post post = getParticipants(4, 1);

        when(postReader.getPostById(postId)).thenReturn(Optional.of(post));

        // when
        BaseException exception = assertThrows(BaseException.class, () -> {
            postReaderService.getParticipantListByPending(loginId, postId, 0, 10);
        });

        assertEquals(NO_VIEWING_PERMISSION.getMessage(), exception.getMessage());
    }
    @Test
    @DisplayName("게시글 참가자 목록 조회 성공")
    void getParticipantListByAccepted_게시글_참가자_목록_조회_성공() {
        // given
        Long postId = 1L;
        Long loginId = 1L;
        Post post = getParticipants(4, 1);

        when(postReader.getPostById(postId)).thenReturn(Optional.of(post));

        // when
        PagedResponse<ParticipantResponse> result = postReaderService.getParticipantListByAccepted(loginId, postId, 0, 10);

        // then
        assertThat(result.contents()).hasSize(1);
    }

    @Test
    @DisplayName("게시글 참가자 목록 조회 예외 - 게시글 소유자 아님")
    void getParticipantListByPending_게시글_신청자_목록_조회_예외() {
        // given
        Long postId = 1L;
        Long loginId = 2L;
        Post post = getParticipants(4, 1);

        when(postReader.getPostById(postId)).thenReturn(Optional.of(post));

        // when
        BaseException exception = assertThrows(BaseException.class, () -> {
            postReaderService.getParticipantListByAccepted(loginId, postId, 0, 10);
        });

        assertEquals(NO_VIEWING_PERMISSION.getMessage(), exception.getMessage());
    }


    //endregion

    //region 헬퍼메서드
    private Post getPost(Long authorId) {
        Post mockPost = Mockito.mock(Post.class);
        lenient().when(mockPost.getId()).thenReturn(1L);
        lenient().when(mockPost.getAuthorId()).thenReturn(authorId);
        lenient().when(mockPost.getAgeLimit()).thenReturn(AgeLimit.AGE_20S); // 유저 조건과 일치
        lenient().when(mockPost.getGenderLimit()).thenReturn(Gender.ALL);    // 통과 가능
        lenient().when(mockPost.getJobCategoryLimit()).thenReturn(JobCategory.ALL); // 통과 가능
        lenient().when(mockPost.getMaxParticipants()).thenReturn(5);
        lenient().when(mockPost.getIsFirstCome()).thenReturn(false); // 일반 게시글

        return mockPost;
    }

    private PostParticipant getParticipant(JoinStatus status) {
        PostParticipant participant = mock(PostParticipant.class);
        lenient().when(participant.getStatus()).thenReturn(status);
        return participant;
    }

    private Post getParticipants(int total, int accepted) {
        Post post = getPost(1L);

        List<PostParticipant> list = new ArrayList<>();
        for (int i = 0; i < accepted; i++) {
            PostParticipant postParticipant = getParticipant(JoinStatus.ACCEPTED);
            list.add(postParticipant);
            lenient().when(postParticipant.getPost()).thenReturn(post);
        }
        for (int i = 0; i < total - accepted; i++) {
            PostParticipant postParticipant = getParticipant(JoinStatus.PENDING);
            list.add(postParticipant);
            lenient().when(postParticipant.getPost()).thenReturn(post);
        }

        lenient().when(post.getPostParticipants()).thenReturn(list);
        return post;
    }
    //endregion
}