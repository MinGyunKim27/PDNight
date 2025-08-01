package org.example.pdnight.domain.post.application.port;

import org.example.pdnight.domain.post.domain.post.Post;

import java.util.Optional;

public interface PostPort {

    Optional<Post> findById(Long postId);

}
