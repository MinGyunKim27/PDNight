package org.example.pdnight.domain.user.infra.reviewInfra;

import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.user.domain.entity.Review;
import org.example.pdnight.domain.user.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewJpaRepository extends JpaRepository<Review, Long> {
    boolean existsByReviewerAndRatedUserAndPost(User reviewer, User ratedUser, Post post);

    Page<Review> findByRatedUser(User ratedUser, Pageable pageable);

    Page<Review> findByReviewer(User reviewer, Pageable pageable);
}
