package org.example.pdnight.domain1.review.repository;

import org.example.pdnight.domain1.post.entity.Post;
import org.example.pdnight.domain1.review.entity.Review;
import org.example.pdnight.domain1.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByReviewerAndRatedUserAndPost(User reviewer, User ratedUser, Post post);

    Page<Review> findByRatedUser(User ratedUser, Pageable pageable);

    Page<Review> findByReviewer(User reviewer, Pageable pageable);
}
