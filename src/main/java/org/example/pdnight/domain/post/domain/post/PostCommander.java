package org.example.pdnight.domain.post.domain.post;

import org.example.pdnight.domain.post.enums.PostStatus;

import java.util.List;
import java.util.Optional;

public interface PostCommander {

    Optional<Post> findByIdAndStatus(Long id, PostStatus status);

    boolean existsByIdAndIsDeletedIsFalse(Long id);

    Optional<Post> findByIdAndIsDeletedIsFalse(Long id);

    void deletePost(Post post);

    Post save(Post post);

    List<Post> findAllByAuthorId(Long authorId);

    void saveES(Post foundPost);
}
