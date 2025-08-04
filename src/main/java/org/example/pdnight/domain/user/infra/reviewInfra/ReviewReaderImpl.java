package org.example.pdnight.domain.user.infra.reviewInfra;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.entity.QReview;
import org.example.pdnight.domain.user.domain.entity.Review;
import org.example.pdnight.domain.user.domain.reviewDomain.ReviewReader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewReaderImpl implements ReviewReader {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Review> findByReviewee(Long userId, Pageable pageable) {
        QReview review = QReview.review;

        List<Review> content = queryFactory
                .selectFrom(review)
                .where(review.revieweeId.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Count 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(review.count())
                .from(review)
                .where(review.revieweeId.eq(userId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<Review> findByReviewer(Long userId, Pageable pageable) {
        QReview review = QReview.review;

        List<Review> content = queryFactory
                .selectFrom(review)
                .where(review.reviewerId.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Count 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(review.count())
                .from(review)
                .where(review.reviewerId.eq(userId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public boolean isExistsByUsersAndPost(Long reviewerId, Long revieweeId, Long postId) {
        QReview review = QReview.review;
        Integer findReview = queryFactory
                .selectOne()
                .from(review)
                .where(
                        review.reviewerId.eq(reviewerId),
                        review.revieweeId.eq(revieweeId),
                        review.postId.eq(postId)
                )
                .fetchFirst();

        return findReview != null;
    }
}
