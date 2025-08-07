package org.example.pdnight.domain.post.application.PostUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.domain.post.PostCommander;
import org.example.pdnight.global.event.AuthDeletedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostConsumerService {
    private final PostCommander postCommander;

    @Transactional
    public void handleAuthDelete(AuthDeletedEvent event) {
        List<Post> posts = postCommander.findAllByAuthorId(event.authId());
        for (Post post : posts) {
            post.removeAuthor();
        }
    }
}
