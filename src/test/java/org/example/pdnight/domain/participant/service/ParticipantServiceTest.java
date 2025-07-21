package org.example.pdnight.domain.participant.service;

import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.participant.dto.response.ParticipantResponse;
import org.example.pdnight.domain.participant.entity.PostParticipant;
import org.example.pdnight.domain.participant.enums.JoinStatus;
import org.example.pdnight.domain.participant.repository.ParticipantRepository;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.post.repository.PostRepository;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private ParticipantService participantService;

    private User mockUser;
    private Post mockPost;

    public static final Long USER_ID = 1L;
    public static final Long POST_ID = 1L;

    @BeforeEach
    void setUp() {
        // set mock User
        mockUser = Mockito.mock(User.class);
        lenient().when(mockUser.getId()).thenReturn(1L);

        // set mock Post
        mockPost = Mockito.mock(Post.class);
        lenient().when(mockPost.getId()).thenReturn(1L);
        lenient().when(mockPost.getStatus()).thenReturn(PostStatus.OPEN);

        // get User and Post
        lenient().when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockUser));
        lenient().when(postRepository.findById(POST_ID)).thenReturn(Optional.of(mockPost));
    }

    @Test
    @DisplayName("참여 신청 성공")
    void applyParticipantTest() {
        //given
        // set mock PostParticipant
        PostParticipant participant = Mockito.mock(PostParticipant.class);
        lenient().when(participant.getStatus()).thenReturn(JoinStatus.PENDING);
        lenient().when(participant.getUser()).thenReturn(mockUser);
        lenient().when(participant.getPost()).thenReturn(mockPost);


        //when
        when(participantRepository.findByUserAndPost(mockUser, mockPost)).thenReturn(Collections.emptyList());
        when(participantRepository.save(any(PostParticipant.class))).thenReturn(participant);

        // run method
        ParticipantResponse response = participantService.applyParticipant(USER_ID, POST_ID);


        //then
        assertNotNull(response);
        assertEquals(participant.getStatus(), response.getStatus());
        assertEquals(participant.getPost().getId(), response.getPostId());
        assertEquals(participant.getUser().getId(), response.getUserId());
    }

    @Test
    @DisplayName("참여 신청 취소 성공")
    void deleteParticipantTest() {
        //given
        // set mock PostParticipant
        PostParticipant participant = Mockito.mock(PostParticipant.class);
        lenient().when(participant.getStatus()).thenReturn(JoinStatus.PENDING);
        lenient().when(participant.getUser()).thenReturn(mockUser);
        lenient().when(participant.getPost()).thenReturn(mockPost);


        //when
        when(participantRepository.findByUserAndPost(mockUser, mockPost))
                .thenReturn(Collections.singletonList(participant));

        // run method
        participantService.deleteParticipant(USER_ID, POST_ID);


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
        JoinStatus joinStatus = JoinStatus.of(status);
        PostParticipant participant = PostParticipant.create(mockPost, mockUser);

        //when
        when(participantRepository.findByUserAndPost(mockUser, mockPost))
                .thenReturn(Collections.singletonList(participant));

        // run method
        ParticipantResponse response = participantService.changeStatusParticipant(USER_ID, POST_ID, status);


        //then
        assertNotNull(response);
        assertEquals(joinStatus, response.getStatus());
        assertEquals(participant.getPost().getId(), response.getPostId());
        assertEquals(participant.getUser().getId(), response.getUserId());
    }

    @Test
    @DisplayName("신청자 목록 조회 성공")
    void getPendingParticipantList() {
        String status = "pending";
        getParticipantList(status);
    }


    @Test
    @DisplayName("참여자 목록 조회 성공")
    void getAcceptedParticipantList() {
        String status = "accepted";
        getParticipantList(status);
    }

    private void getParticipantList(String status) {
        //given
        JoinStatus joinStatus = JoinStatus.of(status);
        int page = 1;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);

        // set mock PostParticipant
        PostParticipant participant = Mockito.mock(PostParticipant.class);
        lenient().when(participant.getStatus()).thenReturn(joinStatus);
        lenient().when(participant.getUser()).thenReturn(mockUser);
        lenient().when(participant.getPost()).thenReturn(mockPost);

        // get Page PostParticipant
        List<PostParticipant> participantList = Collections.singletonList(participant);
        Page<PostParticipant> participantPage = new PageImpl<>(participantList, pageable, participantList.size());


        //when
        when(participantRepository.findByPostAndStatus(mockPost, joinStatus, pageable))
                .thenReturn(participantPage);

        // run method
        PagedResponse<ParticipantResponse> response = participantService.getParticipantListByStatus(USER_ID, joinStatus, page, size);


        //then
        // page
        assertNotNull(response);
        assertEquals(size, response.size());
        assertEquals(page, response.number());
        // contents
        assertEquals(1, response.contents().size());
        assertEquals(joinStatus, response.contents().get(0).getStatus());
        assertEquals(mockUser.getId(), response.contents().get(0).getUserId());
        assertEquals(mockPost.getId(), response.contents().get(0).getPostId());
    }

}
