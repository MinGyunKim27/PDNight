package org.example.pdnight.domain.promotion.infra;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.promotion.domain.PromotionReader;
import org.example.pdnight.domain.promotion.domain.entity.Promotion;
import org.example.pdnight.domain.promotion.domain.entity.PromotionParticipant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.example.pdnight.domain.promotion.domain.entity.QPromotion.promotion;
import static org.example.pdnight.domain.promotion.domain.entity.QPromotionParticipant.promotionParticipant;


@Repository
@RequiredArgsConstructor
public class PromotionReaderImpl implements PromotionReader {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Promotion> findById(Long id) {
        return queryFactory.selectFrom(promotion)
                .where(promotion.id.eq(id))
                .stream()
                .findFirst();
    }

    @Override
    public Page<Promotion> findAllPromotion(Pageable pageable) {
        List<Promotion> content = queryFactory
                .selectFrom(promotion)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Count 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(promotion.count())
                .from(promotion);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);

    }

    @Override
    public boolean existsPromotionByIdAndUserId(Long promotionId, Long userId) {
        Integer existOne = queryFactory
                .selectOne()
                .from(promotionParticipant)
                .where(
                        promotionParticipant.promotion.id.eq(promotionId),
                        promotionParticipant.userId.eq(userId)
                )
                .fetchFirst();

        return existOne != null && existOne > 0;
    }


    // 프로모션에 현재 참가된 인원 수
    @Override
    public Long getPromotionParticipantByPromotionId(Long promotionId) {
        return queryFactory
                .select(promotionParticipant.count())
                .from(promotionParticipant)
                .where(promotionParticipant.promotion.id.eq(promotionId))
                .fetchFirst();
    }

    // 프로모션에 참가 신청한 유저 목록 조회
    @Override
    public Page<PromotionParticipant> findByPromotionWithUser(Promotion promotion, Pageable pageable) {
        List<PromotionParticipant> content = queryFactory
                .selectFrom(promotionParticipant)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Count 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(promotionParticipant.count())
                .from(promotionParticipant)
                .where(promotionParticipant.promotion.id.eq(promotion.getId()));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);

    }

    @Override
    public Page<Promotion> getMyParticipantPromotions(Long userId, Pageable pageable) {
        List<Promotion> content = queryFactory
                .selectFrom(promotion)
                .leftJoin(promotion.promotionParticipants, promotionParticipant).fetchJoin()
                .where(promotionParticipant.userId.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Count 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(promotion.count())
                .from(promotion);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
