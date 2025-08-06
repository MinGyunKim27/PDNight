package org.example.pdnight.domain.promotion.domain;

import org.example.pdnight.domain.promotion.domain.entity.Promotion;
import org.example.pdnight.domain.promotion.domain.entity.PromotionParticipant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface PromotionReader {

    Optional<Promotion> findById(Long id);

    Page<Promotion> findAllPromotion(Pageable pageable);

    boolean existsPromotionByIdAndUserId(Long promotionId, Long userId);

    Long getPromotionParticipantByPromotionId(Long promotionId);

    Page<PromotionParticipant> findByPromotionWithUser(Promotion promotion, Pageable pageable);

    Page<Promotion> getMyParticipantPromotions(Long userId, Pageable pageable);
}
