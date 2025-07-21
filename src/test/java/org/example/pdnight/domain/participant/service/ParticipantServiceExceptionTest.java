package org.example.pdnight.domain.participant.service;

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

    @Test
    @DisplayName("참여 신청 실패 : 이미 신청했습니다")
    void fail_pending_applyParticipantTest() {
        //given
        // set mock PostParticipant
        User mockUser = getUser();
        Post mockPost = getPost();
        PostParticipant participant = getPostParticipant(mockUser, mockPost, JoinStatus.PENDING);

        //when
        when(participantRepository.findByUserAndPost(mockUser, mockPost))
                .thenReturn(Collections.singletonList(participant));

        // run method
        BaseException exception = assertThrows(BaseException.class, () ->
                participantService.applyParticipant(USER_ID, POST_ID)
        );

        //then
        assertEquals("이미 신청했습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("참여 신청 실패 : 이미 가입되어있습니다")
    void fail_accepted_applyParticipantTest() {
        //given
        // set mock PostParticipant
        User mockUser = getUser();
        Post mockPost = getPost();
        PostParticipant participant = getPostParticipant(mockUser, mockPost, JoinStatus.ACCEPTED);

        //when
        when(participantRepository.findByUserAndPost(mockUser, mockPost))
                .thenReturn(Collections.singletonList(participant));

        // run method
        BaseException exception = assertThrows(BaseException.class, () ->
                participantService.applyParticipant(USER_ID, POST_ID)
        );

        //then
        assertEquals("이미 가입되어있습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("참여 신청 취소 실패 : 신청하지 않음")
    void fail_not_apply_deleteParticipantTest() {
        //given
        User mockUser = getUser();
        Post mockPost = getPost();

        //when
        when(participantRepository.findByUserAndPost(mockUser, mockPost))
                .thenReturn(Collections.emptyList());

        // run method
        BaseException exception = assertThrows(BaseException.class, () ->
                participantService.deleteParticipant(USER_ID, POST_ID)
        );

        //then
        assertEquals("취소할 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("참여 신청 취소 실패 : 신청하지 않음")
    void fail_accepted_deleteParticipantTest() {
        //given
        User mockUser = getUser();
        Post mockPost = getPost();
        PostParticipant participant = getPostParticipant(mockUser, mockPost, JoinStatus.ACCEPTED);

        //when
        when(participantRepository.findByUserAndPost(mockUser, mockPost))
                .thenReturn(Collections.singletonList(participant));

        // run method
        BaseException exception = assertThrows(BaseException.class, () ->
                participantService.deleteParticipant(USER_ID, POST_ID)
        );

        //then
        assertEquals("취소할 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("참여 상태 변경 실패 : 신청 대기상태 아님")
    void fail_accepted_changeStatusParticipant() {
        //given
        String status = "rejected";
        User mockUser = getUser();
        Post mockPost = getPost();
        PostParticipant participant = getPostParticipant(mockUser, mockPost, JoinStatus.ACCEPTED);

        //when
        when(participantRepository.findByUserAndPost(mockUser, mockPost))
                .thenReturn(Collections.singletonList(participant));

        // run method
        BaseException exception = assertThrows(BaseException.class, () ->
                participantService.changeStatusParticipant(USER_ID, POST_ID, status)
        );

        //then
        assertEquals("수락 혹은 거절할 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("참여 상태 변경 실패 : 대기상태로 변경할 수 없음")
    void fail_to_pending_changeStatusParticipant() {
        //given
        String status = "pending";
        User mockUser = getUser();
        Post mockPost = getPost();
        PostParticipant participant = getPostParticipant(mockUser, mockPost, JoinStatus.PENDING);

        //when
        when(participantRepository.findByUserAndPost(mockUser, mockPost))
                .thenReturn(Collections.singletonList(participant));

        // run method
        BaseException exception = assertThrows(BaseException.class, () ->
                participantService.changeStatusParticipant(USER_ID, POST_ID, status)
        );

        //then
        assertEquals("수락 혹은 거절할 수 없습니다.", exception.getMessage());
    }
}
