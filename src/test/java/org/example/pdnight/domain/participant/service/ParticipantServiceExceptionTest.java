package org.example.pdnight.domain.participant.service;

import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.participant.entity.PostParticipant;
import org.example.pdnight.domain.participant.enums.JoinStatus;
import org.example.pdnight.domain.participant.repository.ParticipantRepository;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParticipantServiceExceptionTest extends BaseParticipantTest {

    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private ParticipantService participantService;

    // 참여 신청
    @Test
    @DisplayName("참여 신청 실패 : 본인 게시글에 신청하는 경우")
    void fail_self_applyParticipantTest() {
        //given
        User author = getUser(1L);
        Post post = getPost(author, 1L);

        //when
        // run method
        BaseException exception = assertThrows(BaseException.class, () ->
                participantService.applyParticipant(1L, 1L)
        );

        //then
        assertEquals(ErrorCode.CANNOT_PARTICIPANT_SELF.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("참여 신청 실패 : 이미 신청했습니다")
    void fail_pending_applyParticipantTest() {
        //given
        User author = getUser(1L);
        User user = getUser(2L);
        Post post = getPost(author, 1L);
        PostParticipant participant = getPostParticipant(user, post, JoinStatus.PENDING);

        //when
        when(participantRepository.findByUserAndPost(user, post))
                .thenReturn(Collections.singletonList(participant));

        // run method
        BaseException exception = assertThrows(BaseException.class, () ->
                participantService.applyParticipant(2L, 1L)
        );

        //then
        assertEquals(ErrorCode.POST_ALREADY_PENDING.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("참여 신청 실패 : 이미 가입되어있습니다")
    void fail_accepted_applyParticipantTest() {
        //given
        User author = getUser(1L);
        User user = getUser(2L);
        Post post = getPost(author, 1L);
        PostParticipant participant = getPostParticipant(user, post, JoinStatus.ACCEPTED);

        //when
        when(participantRepository.findByUserAndPost(user, post))
                .thenReturn(Collections.singletonList(participant));

        // run method
        BaseException exception = assertThrows(BaseException.class, () ->
                participantService.applyParticipant(2L, 1L)
        );

        //then
        assertEquals(ErrorCode.POST_ALREADY_ACCEPTED.getMessage(), exception.getMessage());
    }

    // 참여 신청 취소
    @Test
    @DisplayName("참여 신청 취소 실패 : 신청하지 않음")
    void fail_not_apply_deleteParticipantTest() {
        //given
        User author = getUser(1L);
        User user = getUser(2L);
        Post post = getPost(author, 1L);

        //when
        when(participantRepository.findByUserAndPost(user, post))
                .thenReturn(Collections.emptyList());

        // run method
        BaseException exception = assertThrows(BaseException.class, () ->
                participantService.deleteParticipant(2L, 1L)
        );

        //then
        assertEquals(ErrorCode.CANNOT_CANCEL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("참여 신청 취소 실패 : 이미 수락되어있음")
    void fail_accepted_deleteParticipantTest() {
        //given
        User author = getUser(1L);
        User user = getUser(2L);
        Post post = getPost(author, 1L);
        PostParticipant participant = getPostParticipant(user, post, JoinStatus.ACCEPTED);

        //when
        when(participantRepository.findByUserAndPost(user, post))
                .thenReturn(Collections.singletonList(participant));

        // run method
        BaseException exception = assertThrows(BaseException.class, () ->
                participantService.deleteParticipant(2L, 1L)
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
        User author = getUser(1L);
        User user = getUser(2L);
        Post post = getPost(author, 1L);
        PostParticipant participant = getPostParticipant(user, post, JoinStatus.PENDING);

        //when
        when(participantRepository.findByUserAndPost(user, post))
                .thenReturn(Collections.singletonList(participant));

        // run method
        BaseException exception = assertThrows(BaseException.class, () ->
                participantService.changeStatusParticipant(2L, 2L, 1L, status)
        );

        //then
        assertEquals(ErrorCode.NO_UPDATE_PERMISSION.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("참여 상태 변경 실패 : 신청 대기상태 아님")
    void fail_accepted_changeStatusParticipant() {
        //given
        String status = "rejected";
        User author = getUser(1L);
        User user = getUser(2L);
        Post post = getPost(author, 1L);
        PostParticipant participant = getPostParticipant(user, post, JoinStatus.ACCEPTED);

        //when
        when(participantRepository.findByUserAndPost(user, post))
                .thenReturn(Collections.singletonList(participant));

        // run method
        BaseException exception = assertThrows(BaseException.class, () ->
                participantService.changeStatusParticipant(1L, 2L, 1L, status)
        );

        //then
        assertEquals(ErrorCode.NOT_PARTICIPANT.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("참여 상태 변경 실패 : 대기상태로 변경할 수 없음")
    void fail_to_pending_changeStatusParticipant() {
        //given
        String status = "pending";
        User author = getUser(1L);
        User user = getUser(2L);
        Post post = getPost(author, 1L);
        PostParticipant participant = getPostParticipant(user, post, JoinStatus.PENDING);

        //when
        when(participantRepository.findByUserAndPost(user, post))
                .thenReturn(Collections.singletonList(participant));

        // run method
        BaseException exception = assertThrows(BaseException.class, () ->
                participantService.changeStatusParticipant(1L, 2L, 1L, status)
        );

        //then
        assertEquals(ErrorCode.NOT_CHANGE_PENDING.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("참여자 목록 조회 실패 : 참가자가 아님")
    void fail_NO_VIEWING_PERMISSION_changeStatusParticipant() {
        //given
        User user = getUser(2L);
        Post post = getPost(getUser(1L), 1L);

        //when
        when(participantRepository.findByUserAndPost(user, post))
                .thenReturn(Collections.emptyList());
        // run method
        BaseException exception = assertThrows(BaseException.class, () ->
                participantService.getParticipantListByAccepted(2L, 1L, 1, 5)
        );

        //then
        assertEquals(ErrorCode.NO_VIEWING_PERMISSION.getMessage(), exception.getMessage());
    }
}
