package org.example.pdnight.domain.promotion.application.promotionUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.promotion.presentation.dto.request.PromotionCreateRequest;
import org.example.pdnight.domain.promotion.presentation.dto.response.PromotionParticipantResponse;
import org.example.pdnight.domain.promotion.presentation.dto.response.PromotionResponse;
import org.example.pdnight.global.aop.SaveLog;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionCommanderService promotionCommanderService;
    private final PromotionReaderService promotionReaderService;

    @SaveLog
    @Transactional
    public PromotionResponse createPromotion(PromotionCreateRequest request) {
        return promotionCommanderService.createPromotion(request);
    }

    // 프로모션 조회
    @Override
    public PromotionResponse findPromotionById(Long id) {
        return promotionReaderService.findPromotionById(id);
    }

    @Override
    public PagedResponse<PromotionResponse> findPromotionList(Pageable pageable) {
        return promotionReaderService.findPromotionList(pageable);
    }

    // 프로모션 수정
    @SaveLog
    @Override
    public PromotionResponse updatePromotion(Long id, PromotionCreateRequest request) {
        return promotionCommanderService.updatePromotion(id, request);
    }

    // 프로모션 삭제
    @SaveLog
    @Override
    public void deletePromotionById(Long id) {
        promotionCommanderService.deletePromotionById(id);
    }

    // 참가 신청
    @Override
    public void addParticipant(Long promotionId, Long userId) {
        promotionCommanderService.addParticipant(promotionId, userId);
    }

    // 참가 신청 유저 목록 조회
    @Override
    public PagedResponse<PromotionParticipantResponse> findPromotionParticipantList(Long promotionId, Pageable pageable) {
        return promotionReaderService.findPromotionParticipantList(promotionId, pageable);
    }

    @Override
    public PagedResponse<PromotionResponse> findMyParticipantPromotions(Long userId, Pageable pageable) {
        return promotionReaderService.findMyParticipantPromotions(userId, pageable);
    }
}
