package org.example.pdnight.domain.user.domain.reviewDomain;

import org.example.pdnight.domain.user.domain.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewReader {
    // 받은 리뷰 리스트
    Page<Review> findByReviewee(Long userId, Pageable pageable);

    // 작성한 리뷰 리스트
    Page<Review> findByReviewer(Long userId, Pageable pageable);

    boolean isExistsByUsersAndPost(Long reviewerId, Long revieweeId, Long postId);
}
