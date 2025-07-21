package org.example.pdnight.domain.participant.service;

import org.example.pdnight.domain.participant.entity.PostParticipant;
import org.example.pdnight.domain.participant.enums.JoinStatus;
import org.example.pdnight.domain.post.entity.Post;
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

    static final Long USER_ID = 1L;
    static final Long POST_ID = 1L;

    protected User getUser() {
        User mockUser = Mockito.mock(User.class);
        lenient().when(mockUser.getId()).thenReturn(1L);
        lenient().when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockUser));
        return mockUser;
    }

    protected Post getPost() {
        Post mockPost = Mockito.mock(Post.class);
        lenient().when(mockPost.getId()).thenReturn(1L);
        lenient().when(mockPost.getStatus()).thenReturn(PostStatus.OPEN);
        lenient().when(postRepository.findById(POST_ID)).thenReturn(Optional.of(mockPost));
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
