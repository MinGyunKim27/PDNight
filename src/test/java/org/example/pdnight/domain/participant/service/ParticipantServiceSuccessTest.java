package org.example.pdnight.domain.participant.service;

import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.participant.dto.response.ParticipantResponse;
import org.example.pdnight.domain.participant.entity.PostParticipant;
import org.example.pdnight.domain.common.enums.JoinStatus;
import org.example.pdnight.domain.participant.repository.ParticipantRepository;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceSuccessTest extends BaseParticipantTest {

    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private ParticipantService participantService;

    @Test
    @DisplayName("참여 신청 성공")
    void applyParticipantTest() {
        //given
        User user = getUser(2L);
        Post post = getPost(getUser(1L));
        PostParticipant participant = getPostParticipant(user, post, JoinStatus.PENDING);

        //when
        when(participantRepository.findByUserAndPost(user, post))
                .thenReturn(Collections.emptyList());
        when(participantRepository.save(any(PostParticipant.class))).thenReturn(participant);
        // run method
        ParticipantResponse response = participantService.applyParticipant(2L, 1L);

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
        User user = getUser(2L);
        Post post = getPost(getUser(1L));
        PostParticipant participant = getPostParticipant(user, post, JoinStatus.PENDING);

        //when
        when(participantRepository.findByUserAndPost(user, post))
                .thenReturn(Collections.singletonList(participant));
        // run method
        participantService.deleteParticipant(2L, 1L);

        //then
        verify(participantRepository, times(1)).delete(participant);
    }

    @Test
    @DisplayName("참여 상태 변경 성공 : ACCEPTED")
    void changeStatusToAcceptedParticipant() {
        String status = "accepted";
        changeStatusParticipant(status);
    }

    @Test
    @DisplayName("참여 상태 변경 성공 : REJECTED")
    void changeStatusParticipant() {
        String status = "rejected";
        changeStatusParticipant(status);
    }

    private void changeStatusParticipant(String status) {
        //given
        User user = getUser(2L);
        Post post = getPost(getUser(1L));
        JoinStatus joinStatus = JoinStatus.of(status);
        PostParticipant participant = PostParticipant.create(post, user);

        //when
        when(participantRepository.findByUserAndPost(user, post))
                .thenReturn(Collections.singletonList(participant));

        // run method
        ParticipantResponse response = participantService.changeStatusParticipant(1L, 2L, 1L, status);

        //then
        assertNotNull(response);
        assertEquals(joinStatus, response.getStatus());
        assertEquals(1L, response.getPostId());
        assertEquals(2L, response.getUserId());
    }

    @Test
    @DisplayName("신청자 목록 조회 성공")
    void getPendingParticipantList() {
        //given
        int page = 1;
        int size = 5;
        User user = getUser(2L);
        Post post = getPost(getUser(1L));
        JoinStatus joinStatus = JoinStatus.PENDING;
        Pageable pageable = PageRequest.of(page, size);
        PostParticipant participant = getPostParticipant(user, post, joinStatus);

        // get Page PostParticipant
        List<PostParticipant> participantList = Collections.singletonList(participant);
        Page<PostParticipant> participantPage = new PageImpl<>(participantList, pageable, participantList.size());


        //when
        when(participantRepository.findByPostAndStatus(post, joinStatus, pageable))
                .thenReturn(participantPage);

        // run method
        PagedResponse<ParticipantResponse> response = participantService.getParticipantListByPending(1L, 1L, page, size);

        //then
        // page
        assertNotNull(response);
        assertEquals(size, response.size());
        assertEquals(page, response.number());
        // contents
        assertEquals(1, response.contents().size());
        assertEquals(joinStatus, response.contents().get(0).getStatus());
        assertEquals(user.getId(), response.contents().get(0).getUserId());
        assertEquals(post.getId(), response.contents().get(0).getPostId());
    }


    @Test
    @DisplayName("참여자 목록 조회 성공 : 참가자")
    void getAcceptedParticipantList() {
        //given
        int page = 1;
        int size = 5;
        User user = getUser(2L);
        Post post = getPost(getUser(1L));
        JoinStatus joinStatus = JoinStatus.ACCEPTED;
        Pageable pageable = PageRequest.of(page, size);
        PostParticipant participant = getPostParticipant(user, post, joinStatus);

        // get Page PostParticipant
        List<PostParticipant> participantList = Collections.singletonList(participant);
        Page<PostParticipant> participantPage = new PageImpl<>(participantList, pageable, participantList.size());


        //when
        when(participantRepository.findByPostAndStatus(post, joinStatus, pageable))
                .thenReturn(participantPage);

        // run method
        PagedResponse<ParticipantResponse> response = participantService.getParticipantListByAccepted(1L, 1L, page, size);

        //then
        // page
        assertNotNull(response);
        assertEquals(size, response.size());
        assertEquals(page, response.number());
        // contents
        assertEquals(1, response.contents().size());
        assertEquals(joinStatus, response.contents().get(0).getStatus());
        assertEquals(user.getId(), response.contents().get(0).getUserId());
        assertEquals(post.getId(), response.contents().get(0).getPostId());
    }

    @Test
    @DisplayName("참여자 목록 조회 성공 : 게시글 주인")
    void getAcceptedParticipantList_with_Author() {
        //given
        int page = 1;
        int size = 5;
        User user = getUser(2L);
        Post post = getPost(getUser(1L));
        JoinStatus joinStatus = JoinStatus.ACCEPTED;
        Pageable pageable = PageRequest.of(page, size);
        PostParticipant participant = getPostParticipant(user, post, joinStatus);

        // get Page PostParticipant
        List<PostParticipant> participantList = Collections.singletonList(participant);
        Page<PostParticipant> participantPage = new PageImpl<>(participantList, pageable, participantList.size());


        //when
        when(participantRepository.findByPostAndStatus(post, joinStatus, pageable))
                .thenReturn(participantPage);

        // run method
        PagedResponse<ParticipantResponse> response = participantService.getParticipantListByAccepted(1L, 1L, page, size);

        //then
        // page
        assertNotNull(response);
        assertEquals(size, response.size());
        assertEquals(page, response.number());
        // contents
        assertEquals(1, response.contents().size());
        assertEquals(joinStatus, response.contents().get(0).getStatus());
        assertEquals(user.getId(), response.contents().get(0).getUserId());
        assertEquals(post.getId(), response.contents().get(0).getPostId());
    }


}
