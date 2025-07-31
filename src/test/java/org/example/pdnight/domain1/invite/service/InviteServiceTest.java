package org.example.pdnight.domain1.invite.service;

import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;
import org.example.pdnight.global.common.helper.GetHelper;
import org.example.pdnight.domain1.invite.dto.response.InviteResponseDto;
import org.example.pdnight.domain1.invite.entity.Invite;
import org.example.pdnight.domain1.invite.repository.InviteRepository;
import org.example.pdnight.domain1.invite.repository.InviteRepositoryQuery;
import org.example.pdnight.domain1.post.entity.Post;
import org.example.pdnight.domain1.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InviteServiceTest {

    @Mock InviteRepository inviteRepository;
    @Mock InviteRepositoryQuery inviteRepositoryQuery;
    @Mock GetHelper helper;
    @InjectMocks
    InviteService inviteService;

    @Test
    @DisplayName("초대 생성 성공")
    void createInvite_success() {
        // given
        Long postId = 1L, userId = 2L, loginUserId = 3L;
        Post post = Mockito.mock(Post.class);
        // User inviter = new User(loginUserId, "초대자", 0L, 0L);
        // User invitee = new User(userId, "피초대자", 0L, 0L);
        User inviter = mock(User.class);
        User invitee = mock(User.class);

        when(helper.getPostByIdOrElseThrow(postId)).thenReturn(post);
        when(helper.getUserByIdOrElseThrow(userId)).thenReturn(invitee);
        when(helper.getUserByIdOrElseThrow(loginUserId)).thenReturn(inviter);
        when(inviteRepository.existsByPostIdAndInviteeIdAndInviterId(postId, userId, loginUserId)).thenReturn(false);

        Invite mockInvite = Invite.create(inviter, invitee, post);
        when(inviteRepository.save(any(Invite.class))).thenReturn(mockInvite);

        // when
        InviteResponseDto response = inviteService.createInvite(postId, userId, loginUserId);

        // then
        assertNotNull(response);
        assertEquals(invitee.getId(), response.getInviteeId());
    }

    @Test
    @DisplayName("초대 삭제 성공")
    void deleteInvite_success() {
        // given
        Long inviteId = 1L;
        Long loginUserId = 3L;
        Post post = Mockito.mock(Post.class);
        // User inviter = new User(loginUserId, "초대자", 0L, 0L);
        // Invite invite = Invite.create(inviter, new User(2L, "초대받은이", 0L, 0L), post);
        User inviter = mock();
        Invite invite = mock();
        when(invite.getInviter()).thenReturn(inviter);
        when(inviter.getId()).thenReturn(loginUserId);

        when(inviteRepository.findById(inviteId)).thenReturn(Optional.of(invite));

        // when
        inviteService.deleteInvite(inviteId, loginUserId);

        // then
        verify(inviteRepository, times(1)).delete(invite);
    }

    @Test
    @DisplayName("내가 초대한 리스트 조회")
    void getMyInvite_success() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        InviteResponseDto dto = mock(InviteResponseDto.class);
        Page<InviteResponseDto> mockPage = new PageImpl<>(List.of(dto), pageable, 1);

        when(inviteRepositoryQuery.getMyInvite(userId, pageable)).thenReturn(mockPage);

        // when
        var response = inviteService.getMyInvite(userId, pageable);

        // then
        assertEquals(1, response.contents().size());
    }

    @Test
    @DisplayName("내가 받은 초대 리스트 조회")
    void getMyInvited_success() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        InviteResponseDto dto = mock(InviteResponseDto.class);
        Page<InviteResponseDto> mockPage = new PageImpl<>(List.of(dto), pageable, 1);

        when(inviteRepositoryQuery.getMyInvited(userId, pageable)).thenReturn(mockPage);

        // when
        var response = inviteService.getMyInvited(userId, pageable);

        // then
        assertEquals(1, response.contents().size());
    }

    @Test
    @DisplayName("중복 초대 예외")
    void createInvite_duplicate_fail() {
        // given
        Long postId = 1L, userId = 2L, loginUserId = 3L;

        when(inviteRepository.existsByPostIdAndInviteeIdAndInviterId(postId, userId, loginUserId)).thenReturn(true);

        // when & then
        BaseException ex = assertThrows(BaseException.class,
                () -> inviteService.createInvite(postId, userId, loginUserId));

        assertEquals(ErrorCode.INVITE_ALREADY_EXISTS.getMessage(), ex.getMessage());
    }

    @Test
    @DisplayName("내 초대가 아닌 경우 삭제 시 예외")
    void deleteInvite_unauthorized_fail() {
        // given
        Long inviteId = 1L;
        Long loginUserId = 99L;
        Post post = Mockito.mock(Post.class);

        User inviter = User.createTestUser(1L, "다른사람", "1@a.com", "");
        Invite invite = Invite.create(inviter, User.createTestUser(2L, "초대받은이", "2@b.com", ""), post);

        when(inviteRepository.findById(inviteId)).thenReturn(Optional.of(invite));

        // when & then
        BaseException ex = assertThrows(BaseException.class,
                () -> inviteService.deleteInvite(inviteId, loginUserId));
        assertEquals(ErrorCode.INVITE_UNAUTHORIZED.getMessage(), ex.getMessage());
    }
}
