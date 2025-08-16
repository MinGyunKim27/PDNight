package org.example.pdnight.domain.post.domain.postReview;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PostReviewReader {

    Page<PostReviewDocument> ReviewPage(Pageable pageable, Long postId);

    Optional<PostReviewDocument> findById(String reviewId);
}
