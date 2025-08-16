package org.example.pdnight.domain.post.infra.postReview;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.postReview.PostReviewDocument;
import org.example.pdnight.domain.post.domain.postReview.PostReviewReader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostReviewReaderImpl implements PostReviewReader {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<PostReviewDocument> ReviewPage(Pageable pageable, Long postId) {
        Query query = new Query().with(pageable);
        if (postId != null) {
            query.addCriteria(Criteria.where("postId").is(postId));
        }
        List<PostReviewDocument> postReviewDocuments = mongoTemplate.find(query, PostReviewDocument.class);
        return PageableExecutionUtils.getPage(postReviewDocuments, pageable, () -> mongoTemplate.count(query, PostReviewDocument.class));
    }

    @Override
    public Optional<PostReviewDocument> findById(String reviewId) {
        PostReviewDocument findReview = mongoTemplate.findById(reviewId, PostReviewDocument.class);
        return Optional.ofNullable(findReview);
    }
}
