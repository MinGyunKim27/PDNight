package org.example.pdnight.domain.promotion.application.promotionUseCase;

import org.example.pdnight.domain.promotion.presentation.dto.request.PromotionCreateRequest;
import org.example.pdnight.domain.promotion.presentation.dto.response.PromotionParticipantResponse;
import org.example.pdnight.domain.promotion.presentation.dto.response.PromotionResponse;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface PromotionService {
    PromotionResponse createPromotion(PromotionCreateRequest request);

    PromotionResponse findPromotionById(Long id);

    PagedResponse<PromotionResponse> findPromotionList(Pageable pageable);

    PromotionResponse updatePromotion(Long id, PromotionCreateRequest request);

    void deletePromotionById(Long id);

    void addParticipant(Long promotionId, Long userId);

    PagedResponse<PromotionParticipantResponse> findPromotionParticipantList(Long promotionId, Pageable pageable);

    PagedResponse<PromotionResponse> findMyParticipantPromotions(Long userId, Pageable pageable);
}
