package org.example.pdnight.domain.participant.service;

import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.participant.entity.PostParticipant;
import org.example.pdnight.domain.common.enums.JoinStatus;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.post.repository.PostRepository;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class BaseParticipantTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    protected User getUser(Long userId) {
        User mockUser = Mockito.mock(User.class);
        lenient().when(mockUser.getId()).thenReturn(userId);
        lenient().when(mockUser.getAge()).thenReturn(25L); // ✅ AgeLimit.AGE_20S
        lenient().when(mockUser.getGender()).thenReturn(Gender.MALE);
        lenient().when(mockUser.getJobCategory()).thenReturn(JobCategory.DATA_SCIENTIST);

        lenient().when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        return mockUser;
    }

    protected Post getPost(User user, Long postId) {
        Post mockPost = Mockito.mock(Post.class);
        lenient().when(mockPost.getId()).thenReturn(postId);
        lenient().when(mockPost.getAuthor()).thenReturn(user);
        lenient().when(mockPost.getAgeLimit()).thenReturn(AgeLimit.AGE_20S); // ✅ 유저 조건과 일치
        lenient().when(mockPost.getGenderLimit()).thenReturn(Gender.ALL);    // ✅ 통과 가능
        lenient().when(mockPost.getJobCategoryLimit()).thenReturn(JobCategory.ALL); // ✅ 통과 가능
        lenient().when(mockPost.getMaxParticipants()).thenReturn(5);
        lenient().when(mockPost.getIsFirstCome()).thenReturn(false); // 일반 게시글

        lenient().when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
        lenient().when(postRepository.findByIdAndStatus(postId, PostStatus.OPEN)).thenReturn(Optional.of(mockPost));
        return mockPost;
    }


    protected PostParticipant getPostParticipant(User user, Post post, JoinStatus status) {
        PostParticipant participant = Mockito.mock(PostParticipant.class);
        lenient().when(participant.getStatus()).thenReturn(status);
        lenient().when(participant.getUser()).thenReturn(user);
        lenient().when(participant.getPost()).thenReturn(post);
        return participant;
    }
}
