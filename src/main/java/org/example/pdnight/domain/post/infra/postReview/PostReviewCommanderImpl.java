package org.example.pdnight.domain.post.infra.postReview;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.postReview.PostReviewCommander;
import org.example.pdnight.domain.post.domain.postReview.PostReviewDocument;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostReviewCommanderImpl implements PostReviewCommander {
    private final PostReviewMongoRepository postReviewMongoRepository;

    @Override
    public Optional<PostReviewDocument> findById(String reviewId) {
        return postReviewMongoRepository.findById(reviewId);
    }

    @Override
    public PostReviewDocument save(PostReviewDocument postReview) {
        return postReviewMongoRepository.save(postReview);
    }

    @Override
    public void delete(PostReviewDocument reviewId) {
        postReviewMongoRepository.delete(reviewId);
    }
}
