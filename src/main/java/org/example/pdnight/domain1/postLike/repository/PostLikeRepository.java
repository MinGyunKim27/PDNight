package org.example.pdnight.domain1.postLike.repository;

import org.example.pdnight.domain1.post.entity.Post;
import org.example.pdnight.domain1.postLike.entity.PostLike;
import org.example.pdnight.domain1.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndUser(Post post, User user);

    boolean existsByPostAndUser(Post post, User user);
}
