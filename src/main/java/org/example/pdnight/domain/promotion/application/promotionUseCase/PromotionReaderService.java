package org.example.pdnight.domain.promotion.application.promotionUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.promotion.domain.PromotionReader;
import org.example.pdnight.domain.promotion.domain.entity.Promotion;
import org.example.pdnight.domain.promotion.domain.entity.PromotionParticipant;
import org.example.pdnight.domain.promotion.presentation.dto.response.PromotionParticipantResponse;
import org.example.pdnight.domain.promotion.presentation.dto.response.PromotionResponse;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PromotionReaderService {

    private final PromotionReader promotionReader;

    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public PromotionResponse findPromotionById(Long id) {
        Promotion promotion = getPromotionById(id);

        return PromotionResponse.from(promotion);
    }

    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public PagedResponse<PromotionResponse> findPromotionList(Pageable pageable) {
        Page<Promotion> promotionPage = promotionReader.findAllPromotion(pageable);
        return PagedResponse.from(promotionPage.map(PromotionResponse::from));
    }

    // 참가 신청 유저 목록 조회
    public PagedResponse<PromotionParticipantResponse> findPromotionParticipantList(Long promotionId, Pageable pageable) {
        Promotion promotion = getPromotionById(promotionId);
        Page<PromotionParticipant> promotionPage = promotionReader.findByPromotionWithUser(promotion, pageable);
        return PagedResponse.from(promotionPage.map(PromotionParticipantResponse::from));
    }

    public PagedResponse<PromotionResponse> findMyParticipantPromotions(Long userId, Pageable pageable) {
        Page<Promotion> participantPromotions = promotionReader.getMyParticipantPromotions(userId, pageable);
        return PagedResponse.from(participantPromotions.map(PromotionResponse::from));
    }


    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // get
    private Promotion getPromotionById(Long id) {
        return promotionReader.findById(id).orElseThrow(
                () -> new BaseException(ErrorCode.PROMOTION_NOT_FOUNT)
        );
    }
}
