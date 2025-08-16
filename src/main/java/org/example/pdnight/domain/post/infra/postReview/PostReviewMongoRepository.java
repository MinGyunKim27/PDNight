package org.example.pdnight.domain.post.infra.postReview;

import org.example.pdnight.domain.post.domain.postReview.PostReviewDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostReviewMongoRepository extends MongoRepository<PostReviewDocument, String> {
}
