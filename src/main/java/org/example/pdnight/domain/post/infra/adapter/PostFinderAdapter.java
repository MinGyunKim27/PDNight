package org.example.pdnight.domain.post.infra.adapter;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.application.port.PostPort;
import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.domain.post.PostReader;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostFinderAdapter implements PostPort {

    private final PostReader postReader;

    public Optional<Post> findById(Long postId) {
        return postReader.findById(postId);
    }

}

