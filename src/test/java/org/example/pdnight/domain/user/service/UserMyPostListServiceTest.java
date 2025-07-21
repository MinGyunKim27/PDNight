package org.example.pdnight.domain.user.service;

import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.participant.entity.PostParticipant;
import org.example.pdnight.domain.participant.enums.JoinStatus;
import org.example.pdnight.domain.post.dto.request.PostRequestDto;
import org.example.pdnight.domain.post.dto.response.PostResponseDto;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.repository.PostRepositoryQuery;
import org.example.pdnight.domain.post.repository.PostRepositoryQueryImpl;
import org.example.pdnight.domain.user.entity.User;

import org.example.pdnight.domain.user.dto.response.PostWithJoinStatusAndAppliedAtResponseDto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private PostRepositoryQueryImpl postRepositoryQuery;

    @Test
    void findMyLikedPosts_정상조회() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        User author = mock(User.class);  // 또는 직접 생성해도 됨
        Post post1 = Post.createPost(author, "제목1", LocalDateTime.now(), "공개", "비공개", 4,
                Gender.MALE, JobCategory.BACK_END_DEVELOPER, AgeLimit.AGE_20S);
        Post post2 = Post.createPost(author, "제목2", LocalDateTime.now(), "공개", "비공개", 4,
                Gender.MALE, JobCategory.BACK_END_DEVELOPER, AgeLimit.AGE_20S);

        List<Post> postList = List.of(post1, post2);
        Page<Post> postPage = new PageImpl<>(postList);

        when(postRepositoryQuery.getMyLikePost(userId, pageable)).thenReturn(postPage);

        // when
        PagedResponse<PostResponseDto> response = userService.findMyLikedPosts(userId, pageable);

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
                userService.findMyConfirmedPosts(userId, joinStatus, pageable);

        // then
        assertThat(response.contents()).hasSize(2);
        verify(postRepositoryQuery).getConfirmedPost(userId, joinStatus, pageable);
    }

    @Test
    void findMyWrittenPosts_정상조회() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        List<PostResponseDto> dtoList = List.of(new PostResponseDto(), new PostResponseDto());
        Page<PostResponseDto> page = new PageImpl<>(dtoList);

        when(postRepositoryQuery.getWrittenPost(userId, pageable)).thenReturn(page);

        // when
        PagedResponse<PostResponseDto> response = userService.findMyWrittenPosts(userId, pageable);

        // then
        assertThat(response.contents()).hasSize(2);
        verify(postRepositoryQuery).getWrittenPost(userId, pageable);
    }
}

