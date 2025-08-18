package org.example.pdnight.domain.post.infra.postReview;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.postReview.PostReviewCommander;
import org.example.pdnight.domain.post.domain.postReview.PostReviewDocument;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostReviewCommanderImpl implements PostReviewCommander {

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<PostReviewDocument> findById(String reviewId) {
        return Optional.ofNullable(mongoTemplate.findById(reviewId, PostReviewDocument.class));
    }

    @Override
    public PostReviewDocument save(PostReviewDocument postReview) {
        return mongoTemplate.save(postReview);
    }

    @Override
    public void delete(PostReviewDocument review) {
        mongoTemplate.remove(review);
    }
}
