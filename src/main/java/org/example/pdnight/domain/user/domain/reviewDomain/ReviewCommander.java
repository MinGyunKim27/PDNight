package org.example.pdnight.domain.user.domain.reviewDomain;

import org.example.pdnight.domain.user.domain.entity.Review;

public interface ReviewCommander {
    Review save(Review review);

    boolean isExistsByUsersAndPost(Long reviewerId, Long revieweeId, Long postId);
}
