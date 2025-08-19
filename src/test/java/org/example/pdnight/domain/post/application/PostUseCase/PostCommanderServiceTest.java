package org.example.pdnight.domain.post.application.PostUseCase;

import org.example.pdnight.domain.post.domain.post.*;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.post.presentation.dto.request.PostRequest;
import org.example.pdnight.domain.post.presentation.dto.request.PostUpdateRequest;
import org.example.pdnight.domain.post.presentation.dto.response.InviteResponse;
import org.example.pdnight.domain.post.presentation.dto.response.ParticipantResponse;
import org.example.pdnight.domain.post.presentation.dto.response.PostLikeResponse;
import org.example.pdnight.domain.post.presentation.dto.response.PostResponse;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.enums.JobCategory;
import org.example.pdnight.global.common.exception.BaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostCommanderServiceTest {
    // ------------- post --------------
    @InjectMocks
    private PostCommanderService postCommanderService;

    @Mock
    private PostCommander postCommander;

    @Mock
    private PostProducer postProducer;

    @Mock
    private UserPort userPort;

    @Mock
    private TagPort tagPort;

    @Mock
    private PostInfoAssembler postInfoAssembler;

    private PostRequest postRequest;
    private Post post;
    private Post testPost;

    //테스트 코드 사전 세팅
    @BeforeEach
    void setUp() {
        //요청 DTO 세팅
        List<Long> idList = List.of(1L, 2L);

        postRequest = PostRequest.builder()
                .title("제목")
                .timeSlot(LocalDateTime.now())
                .publicContent("공개 내용")
                .maxParticipants(4)
                .tagIdList(idList)
                .build();

        // 테스트 용 post 객체
        post = Post.createPost(
                1L,
                postRequest.getTitle(),
                postRequest.getTimeSlot(),
                postRequest.getPublicContent(),
                postRequest.getMaxParticipants(),
                postRequest.getGenderLimit(),
                postRequest.getJobCategoryLimit(),
                postRequest.getAgeLimit(),
                false,
                postRequest.getTagIdList()
        );
        post.setStatus(PostStatus.OPEN);

        // 선착순 포스트 생성
        testPost = Post.createPost(
                500L,
                "테스트 제목",
                LocalDateTime.now().plusDays(1),
                "공개 내용",
                50,
                Gender.ALL,
                JobCategory.ALL,
                AgeLimit.ALL,
                true,
                Collections.emptyList()
        );
        testPost = postCommander.save(testPost);
    }

    @Test
    @DisplayName("게시글 등록 테스트")
    void createPost_게시글_등록_테스트() {
        //given
        Long userId = 1L;

        Post post = Post.createPost(userId,
                "제목",
                LocalDateTime.now(),
                "공개 내용",
                4,
                null, null, null, false, Collections.emptyList());
        when(postCommander.save(any())).thenReturn(post);

        when(postInfoAssembler.toDto(any(Post.class))).thenReturn(PostResponse.from(post, Collections.emptyList()));

        when(userPort.findFollowersOf(userId)).thenReturn(Collections.emptyList());


        //when
        PostResponse createPost = postCommanderService.createPost(userId, postRequest);

        //then
        assertNotNull(createPost);
        assertEquals(post.getTitle(), createPost.getTitle());
    }

    @Test
    @DisplayName("내가 작성한 게시글 삭제 시 예외 테스트")
    void deletePostById_다른_작성자_게시글_삭제() {
        //given
        Long postId = 1L;
        //작성자와 다른 Id 일때
        Long userId = 5L;

        when(postCommander.findByIdAndIsDeletedIsFalse(postId)).thenReturn(Optional.of(post));
        //when, then
        BaseException exception = assertThrows(BaseException.class, () -> {
            postCommanderService.deletePostById(userId, postId);
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
        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .title("수정된 제목")
                .timeSlot(LocalDateTime.now())
                .publicContent("수정된 공개 내용")
                .maxParticipants(5)
                .genderLimit(Gender.FEMALE)
                .jobCategoryLimit(JobCategory.BACK_END_DEVELOPER)
                .ageLimit(AgeLimit.AGE_30S)
                .tagIdList(Collections.emptyList())
                .build();

        //
        when(postCommander.findByIdAndIsDeletedIsFalse(postId)).thenReturn(Optional.of(post));

        Post post2 = Post.createPost(
                1L,
                postUpdateRequest.getTitle(),
                postUpdateRequest.getTimeSlot(),
                postUpdateRequest.getPublicContent(),
                postUpdateRequest.getMaxParticipants(),
                postUpdateRequest.getGenderLimit(),
                postUpdateRequest.getJobCategoryLimit(),
                postUpdateRequest.getAgeLimit(),
                false,
                postUpdateRequest.getTagIdList()
        );

        when(postInfoAssembler.toDto(any(Post.class))).thenReturn(PostResponse.from(post2, Collections.emptyList()));
        //when
        PostResponse postResponse = postCommanderService.updatePostDetails(userId, postId, postUpdateRequest);
        //then
        assertNotNull(postResponse);

        //이전 내용과 같으면 안됨
        assertNotEquals(postResponse.getTitle(), oldTitle);
        assertNotEquals(postResponse.getJobCategoryLimit(), oldJobCategoryLimit);

        //변경 내용 확인
        assertEquals(postResponse.getTitle(), post.getTitle());
        assertEquals(postResponse.getJobCategoryLimit(), post.getJobCategoryLimit());
    }

    // 참여 신청
    @Test
    @DisplayName("참여 신청 실패 : 본인 게시글에 신청하는 경우")
    void fail_self_applyParticipantTest() {
        //given
        when(postCommander.findByIdAndIsDeletedIsFalse(1L)).thenReturn(Optional.ofNullable(post));
        //when
        // run method
        BaseException exception = assertThrows(BaseException.class, () ->
                postCommanderService.applyParticipant(1L, 20L, Gender.MALE, JobCategory.BACK_END_DEVELOPER, 1L)
        );

        //then
        assertEquals(ErrorCode.CANNOT_PARTICIPATE_SELF.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("참여 신청 실패 : 이미 신청했습니다")
    void fail_pending_applyParticipantTest() {
        //given
        Long userId = 5L;
        PostParticipant participant = PostParticipant.create(post, userId);

        post.addParticipants(participant);
        //when
        when(postCommander.findByIdAndIsDeletedIsFalse(1L)).thenReturn(Optional.ofNullable(post));

        // run method
        BaseException exception = assertThrows(BaseException.class, () ->
                postCommanderService.applyParticipant(userId, 20L, Gender.MALE, JobCategory.BACK_END_DEVELOPER, 1L)
        );

        //then
        assertEquals(ErrorCode.POST_ALREADY_PENDING.getMessage(), exception.getMessage());
    }

    // 참여 신청 취소
    @Test
    @DisplayName("참여 신청 취소 실패 : 신청하지 않음")
    void fail_not_apply_deleteParticipantTest() {
        //given
        Long authorId = 1L;
        Long userId = 2L;

        when(postCommander.findByIdAndIsDeletedIsFalse(1L)).thenReturn(Optional.ofNullable(post));

        //when
        BaseException exception = assertThrows(BaseException.class, () ->
                postCommanderService.deleteParticipant(userId, authorId)
        );

        //then
        assertEquals(ErrorCode.CANNOT_CANCEL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("참여 신청 취소 실패 : 이미 수락되어있음")
    void fail_accepted_deleteParticipantTest() {
        //given
        Long userId = 2L;
        PostParticipant participant = PostParticipant.create(post, userId);
        participant.changeStatus(JoinStatus.ACCEPTED);

        when(postCommander.findByIdAndIsDeletedIsFalse(1L)).thenReturn(Optional.ofNullable(post));

        //when
        BaseException exception = assertThrows(BaseException.class, () ->
                postCommanderService.deleteParticipant(2L, 1L)
        );

        //then
        assertEquals(ErrorCode.CANNOT_CANCEL.getMessage(), exception.getMessage());
    }

    // 참여 상태 변경
    @Test
    @DisplayName("참여 상태 변경 실패 : 게시글이 본인것이 아님")
    void fail_not_my_post_changeStatusParticipant() {
        //given
        String status = "accepted";
        Long userId = 2L;
        PostParticipant participant = PostParticipant.create(post, userId);
        post.addParticipants(participant);
        //when
        when(postCommander.findByIdAndIsDeletedIsFalse(1L)).thenReturn(Optional.ofNullable(post));

        // run method
        BaseException exception = assertThrows(BaseException.class, () ->
                postCommanderService.changeStatusParticipant(2L, 2L, 1L, status)
        );

        //then
        assertEquals(ErrorCode.NO_UPDATE_PERMISSION.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("참여 상태 변경 실패 : 신청 대기상태 아님")
    void fail_accepted_changeStatusParticipant() {
        //given
        String status = "rejected";
        Long userId = 2L;
        PostParticipant participant = PostParticipant.create(post, userId);
        participant.changeStatus(JoinStatus.ACCEPTED);

        //when
        when(postCommander.findByIdAndIsDeletedIsFalse(1L)).thenReturn(Optional.ofNullable(post));

        // run method
        BaseException exception = assertThrows(BaseException.class, () ->
                postCommanderService.changeStatusParticipant(1L, 2L, 1L, status)
        );

        //then
        assertEquals(ErrorCode.NOT_PARTICIPATED.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("참여 상태 변경 실패 : 대기상태로 변경할 수 없음")
    void fail_to_pending_changeStatusParticipant() {
        //given
        String status = "pending";
        Long userId = 2L;
        PostParticipant participant = PostParticipant.create(post, userId);
        post.addParticipants(participant);

        //when
        when(postCommander.findByIdAndIsDeletedIsFalse(1L)).thenReturn(Optional.ofNullable(post));

        // run method
        BaseException exception = assertThrows(BaseException.class, () ->
                postCommanderService.changeStatusParticipant(1L, 2L, 1L, status)
        );

        //then
        assertEquals(ErrorCode.NOT_CHANGE_PENDING.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("참여 신청 성공")
    void applyParticipantTest() {
        //given
        Long userId = 2L;
        PostParticipant participant = PostParticipant.create(post, userId);

        // 신청 안되는지 확인
        when(postCommander.findByIdAndIsDeletedIsFalse(1L)).thenReturn(Optional.ofNullable(post));
        // postProducer 패스
        doNothing().when(postProducer).produce(anyString(), any());

        // when
        ParticipantResponse response = postCommanderService.applyParticipant(userId, 20L, Gender.MALE, JobCategory.BACK_END_DEVELOPER, 1L);

        //then
        assertNotNull(response);
        assertEquals(participant.getStatus(), response.getStatus());
        assertEquals(1L, response.getPostId());
        assertEquals(2L, response.getUserId());
    }

    @Test
    @DisplayName("참여 신청 취소 성공")
    void deleteParticipantTest() {
        //given
        Long userId = 2L;
        PostParticipant participant = PostParticipant.create(post, userId);
        post.addParticipants(participant);

        //when
        when(postCommander.findByIdAndIsDeletedIsFalse(1L)).thenReturn(Optional.ofNullable(post));

        // run method
        postCommanderService.deleteParticipant(2L, 1L);

        //then
        assertFalse(post.getPostParticipants().contains(participant));
    }

    @Test
    @DisplayName("참여 상태 변경 성공 : REJECTED")
    void changeStatusParticipant() {
        //given
        Long userId = 2L;
        PostParticipant participant = PostParticipant.create(post, userId);
        post.addParticipants(participant);

        //when
        when(postCommander.findByIdAndIsDeletedIsFalse(1L)).thenReturn(Optional.ofNullable(post));
        // postProducer 패스
        doNothing().when(postProducer).produce(anyString(), any());

        // run method
        ParticipantResponse response = postCommanderService.changeStatusParticipant(1L, 2L, 1L, "REJECTED");

        //then
        assertNotNull(response);
        assertEquals(JoinStatus.REJECTED, response.getStatus());
        assertEquals(1L, response.getPostId());
        assertEquals(2L, response.getUserId());
    }

    // postlike

    @Test
    @DisplayName("좋아요 등록 성공")
    void 좋아요_등록_성공() {
        // given
        Long userId = 1L;
        Long postId = 1L;

        Post mockPost = Mockito.mock(Post.class);
        PostLike mockPostLike = mock();

        when(postCommander.findByIdAndIsDeletedIsFalse(postId)).thenReturn(Optional.ofNullable(post));

        // when
        PostLikeResponse response = postCommanderService.addLike(postId, userId);

        // then
        assertNotNull(response);
        assertEquals(userId, response.getUserId());
    }

    @Test
    @DisplayName("이미 좋아요를 누른 경우 예외 발생")
    void 중복_좋아요_예외() {
        // given
        Long userId = 1L;
        Long postId = 10L;
        PostLike postLike = PostLike.create(post, userId);
        post.addLike(postLike);
        when(postCommander.findByIdAndIsDeletedIsFalse(postId)).thenReturn(Optional.ofNullable(post));

        BaseException exception = assertThrows(BaseException.class, () ->
                postCommanderService.addLike(postId, userId)
        );

        assertEquals(ErrorCode.ALREADY_LIKED.getStatus(), exception.getStatus());
        assertEquals(ErrorCode.ALREADY_LIKED.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("좋아요 취소 성공")
    void 좋아요_취소_성공() {
        // given
        Long userId = 1L;
        Long postId = 10L;
        PostLike postLike = PostLike.create(post, userId);
        post.addLike(postLike);
        when(postCommander.findByIdAndIsDeletedIsFalse(postId)).thenReturn(Optional.ofNullable(post));

        // when
        postCommanderService.removeLike(postId, userId);

        // then
        assertFalse(post.getPostLikes().contains(postLike));
    }

    @Test
    @DisplayName("좋아요 기록이 존재하지 않을 때 예외 발생")
    void 좋아요_취소_좋아요없음_예외() {
        // given
        Long userId = 1L;
        Long postId = 10L;
        PostLike postLike = PostLike.create(post, userId);

        when(postCommander.findByIdAndIsDeletedIsFalse(postId)).thenReturn(Optional.ofNullable(post));
        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                postCommanderService.removeLike(postId, userId)
        );

        assertEquals(ErrorCode.POSTLIKE_NOT_FOUND.getStatus(), exception.getStatus());
    }


    @Test
    @DisplayName("초대 생성 성공")
    void createInvite_success() {
        // given
        Long postId = 1L, userId = 2L, loginUserId = 3L;

        when(postCommander.findByIdAndIsDeletedIsFalse(postId)).thenReturn(Optional.ofNullable(post));
        // postProducer 패스
        doNothing().when(postProducer).produce(anyString(), any());

        // when
        InviteResponse response = postCommanderService.createInvite(postId, userId, loginUserId);

        // then
        assertNotNull(response);
        assertEquals(userId, response.getInviteeId());
    }

    @Test
    @DisplayName("초대 삭제 성공")
    void deleteInvite_success() {
        // given
        Long inviteId = 1L;
        Long loginUserId = 3L;
        Long postId = 1L;
        Invite invite = Invite.create(loginUserId, inviteId, post);
        post.addInvite(invite);
        when(postCommander.findByIdAndIsDeletedIsFalse(postId)).thenReturn(Optional.ofNullable(post));

        // when
        postCommanderService.deleteInvite(postId, inviteId, loginUserId);

        // then
        assertFalse(post.getInvites().contains(invite));
    }

    @Test
    @DisplayName("중복 초대 예외")
    void createInvite_duplicate_fail() {
        // given
        Long postId = 1L, userId = 2L, loginUserId = 3L;

        Invite invite = Invite.create(loginUserId, userId, post);
        post.addInvite(invite);
        when(postCommander.findByIdAndIsDeletedIsFalse(postId)).thenReturn(Optional.ofNullable(post));

        // when & then
        BaseException ex = assertThrows(BaseException.class,
                () -> postCommanderService.createInvite(postId, userId, loginUserId));

        assertEquals(ErrorCode.INVITE_ALREADY_EXISTS.getMessage(), ex.getMessage());
    }

    @Test
    @DisplayName("내 초대가 아닌 경우 삭제 시 예외")
    void deleteInvite_unauthorized_fail() {
        // given
        Long inviteId = 1L;
        Long loginUserId = 99L;
        Long anotherUserId = 2L;
        Invite invite = Invite.create(inviteId, anotherUserId, post);
        post.addInvite(invite);

        when(postCommander.findByIdAndIsDeletedIsFalse(1L)).thenReturn(Optional.ofNullable(post));

        // when & then
        BaseException ex = assertThrows(BaseException.class,
                () -> postCommanderService.deleteInvite(1L, inviteId, loginUserId));
        assertEquals(ErrorCode.INVITE_UNAUTHORIZED.getMessage(), ex.getMessage());
    }

    @Test
    @DisplayName("초대 승인 성공 테스트")
    void decisionForInvite_초대_정상_승인() {
        //given
        Long postId = 1L;
        Long inviteeId = 1L;
        Long loginUserId = 1L;

        Invite invite = Invite.create(postId, inviteeId, post);
        post.addInvite(invite);
        when(postCommander.findByIdAndIsDeletedIsFalse(postId)).thenReturn(Optional.ofNullable(post));

        //이미 승인된 참가자 한명 넣기
        PostParticipant postParticipant = PostParticipant.create(post, 2L);
        postParticipant.changeStatus(JoinStatus.ACCEPTED);
        post.addParticipants(postParticipant);

        // postProducer 패스
        doNothing().when(postProducer).produce(anyString(), any());

        //when
        postCommanderService.decisionForInvite(postId, loginUserId);

        //then
        PostParticipant findParticipants = post.getPostParticipants().stream()
                .filter(participants -> participants.getUserId().equals(loginUserId))
                .findFirst()
                .orElseThrow();
        assertEquals(JoinStatus.ACCEPTED, findParticipants.getStatus());

        assertEquals(2, post.getPostParticipants().size()); //한명더 추가되었는지
        assertTrue(post.getInvites().isEmpty()); // 초대 삭제 확인
        verify(postCommander).findByIdAndIsDeletedIsFalse(postId);
    }

    @Test
    @DisplayName("초대 승인 받았는데 꽉차서 예외터짐 테스트")
    void decisionForInvite_참여자_최대_초대_승인_실패() {
        //given
        Long postId = 1L;
        Long inviteeId = 1L;
        Long loginUserId = 1L;

        Invite invite = Invite.create(postId, inviteeId, post);
        post.addInvite(invite);
        when(postCommander.findByIdAndIsDeletedIsFalse(postId)).thenReturn(Optional.ofNullable(post));

        //참여 승인된 인원을 MAX로 채움
        PostParticipant postParticipant1 = PostParticipant.create(post, 2L);
        PostParticipant postParticipant2 = PostParticipant.create(post, 3L);
        PostParticipant postParticipant3 = PostParticipant.create(post, 4L);
        PostParticipant postParticipant4 = PostParticipant.create(post, 5L);
        postParticipant1.changeStatus(JoinStatus.ACCEPTED);
        postParticipant2.changeStatus(JoinStatus.ACCEPTED);
        postParticipant3.changeStatus(JoinStatus.ACCEPTED);
        postParticipant4.changeStatus(JoinStatus.ACCEPTED);
        post.addParticipants(postParticipant1);
        post.addParticipants(postParticipant2);
        post.addParticipants(postParticipant3);
        post.addParticipants(postParticipant4);

        //when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                postCommanderService.decisionForInvite(postId, loginUserId));

        assertEquals(ErrorCode.CANNOT_PARTICIPATE_POST.getMessage(), exception.getMessage());
        verify(postCommander).findByIdAndIsDeletedIsFalse(postId);
    }

    @Test
    @DisplayName("초대 거절 성공 테스트")
    void rejectForInvite_초대_정상_거절() {
        //given
        Long postId = 1L;
        Long inviteeId = 1L;
        Long loginUserId = 1L;

        //초대생성
        Invite invite = Invite.create(postId, inviteeId, post);
        post.addInvite(invite);
        when(postCommander.findByIdAndIsDeletedIsFalse(postId)).thenReturn(Optional.ofNullable(post));

        // postProducer 패스
        doNothing().when(postProducer).produce(anyString(), any());

        //when
        postCommanderService.rejectForInvite(postId, loginUserId);

        //then
        assertTrue(post.getInvites().isEmpty()); // 초대 삭제 확인

        verify(postCommander).findByIdAndIsDeletedIsFalse(postId);
    }
}
