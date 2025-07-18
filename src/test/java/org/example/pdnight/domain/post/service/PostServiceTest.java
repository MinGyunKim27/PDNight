package org.example.pdnight.domain.post.service;

import org.example.pdnight.domain.post.repository.PostRepository;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
	@Mock
	private PostRepository postRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private PostService postService;

}