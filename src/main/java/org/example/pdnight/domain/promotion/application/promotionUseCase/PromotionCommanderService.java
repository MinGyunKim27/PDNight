package org.example.pdnight.domain.promotion.application.promotionUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.promotion.domain.PromotionCommander;
import org.example.pdnight.domain.promotion.domain.entity.Promotion;
import org.example.pdnight.domain.promotion.domain.entity.PromotionParticipant;
import org.example.pdnight.domain.promotion.presentation.dto.request.PromotionCreateRequest;
import org.example.pdnight.domain.promotion.presentation.dto.response.PromotionResponse;
import org.example.pdnight.global.aop.DistributedLock;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PromotionCommanderService {

    private final PromotionCommander promotionCommander;

    // 프로모션 생성
    @Transactional
    public PromotionResponse createPromotion(PromotionCreateRequest request) {
        if (request.getMaxParticipants() < 1) {
            throw new BaseException(ErrorCode.PROMOTION_INVALID_PARTICIPANT);
        }

        if (request.getPromotionStartDate().isBefore(LocalDateTime.now())
                || request.getPromotionStartDate().isAfter(request.getPromotionEndDate())
                || request.getPromotionEndDate().isBefore(LocalDateTime.now())
        ) {
            throw new BaseException(ErrorCode.PROMOTION_INVALID_DATE);
        }

        Promotion promotion = Promotion.from(
                request.getTitle(),
                request.getContent(),
                request.getMaxParticipants(),
                request.getPromotionStartDate(),
                request.getPromotionEndDate()
        );

        Promotion savePromotion = promotionCommander.save(promotion);
        return PromotionResponse.from(savePromotion);
    }

    @Transactional
    public PromotionResponse updatePromotion(Long id, PromotionCreateRequest request) {
        Promotion promotion = getPromotionById(id);

        if (request.getMaxParticipants() < 1) {
            throw new BaseException(ErrorCode.PROMOTION_INVALID_PARTICIPANT);
        }

        promotion.updatePromotion(
                request.getTitle(),
                request.getContent(),
                request.getMaxParticipants(),
                request.getPromotionStartDate(),
                request.getPromotionEndDate()
        );

        Promotion savePromotion = promotionCommander.save(promotion);
        return PromotionResponse.from(savePromotion);
    }

    @Transactional
    public void deletePromotionById(Long id) {
        Promotion promotion = getPromotionById(id);
        promotionCommander.delete(promotion);
    }

    @Transactional
    @DistributedLock(
            key = "#promotionId",
            timeoutMs = 5000
    )
    public void addParticipant(Long promotionId, Long userId) {

        Promotion promotion = getPromotionById(promotionId);

        // 이미 참가 신청한 유저이면 실패
        validateParticipant(promotion, userId);

        // 신청 인원 확인
        int participantsCount = promotion.getPromotionParticipants().size();
        if (participantsCount >= promotion.getMaxParticipants()) {
            throw new BaseException(ErrorCode.PROMOTION_PARTICIPANT_FULL);
        }

        PromotionParticipant promotionParticipant = PromotionParticipant.create(promotion, userId);
        promotion.addParticipant(promotionParticipant);
    }


    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // get
    private Promotion getPromotionById(Long id) {
        return promotionCommander.findById(id).orElseThrow(
                () -> new BaseException(ErrorCode.PROMOTION_NOT_FOUNT)
        );
    }

    // validate
    private void validateParticipant(Promotion promotion, Long userId) {
        if (promotion.getPromotionParticipants().stream().anyMatch(participant -> participant.getUserId().equals(userId))) {
            throw new BaseException(ErrorCode.PROMOTION_ALREADY_PENDING);
        }
    }
}
