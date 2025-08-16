package org.example.pdnight.domain.post.domain.postReview;

import java.util.Optional;

public interface PostReviewCommander {
    Optional<PostReviewDocument> findById(String reviewId);

    PostReviewDocument save(PostReviewDocument postReview);

    void delete(PostReviewDocument reviewId);
}
