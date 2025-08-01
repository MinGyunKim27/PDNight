package org.example.pdnight.domain.post.domain.post;

import org.example.pdnight.domain.post.enums.PostStatus;

import java.util.Optional;

public interface PostCommander {

    Optional<Post> findByIdAndStatus(Long id, PostStatus status);

    boolean existsById(Long id);

    Optional<Post> findPostById(Long id);

    Optional<Post> getPostById(Long id);

    void deletePost(Post post);

    Post save(Post post);

    void save(Post post);
}
