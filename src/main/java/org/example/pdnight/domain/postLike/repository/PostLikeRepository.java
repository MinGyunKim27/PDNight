package org.example.pdnight.domain.postLike.repository;

import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.postLike.entity.PostLike;
import org.example.pdnight.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndUser(Post post, User user);

    boolean existsByPostAndUser(Post post, User user);
}
