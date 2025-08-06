package org.example.pdnight.domain.promotion.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.promotion.application.promotionUseCase.PromotionService;
import org.example.pdnight.domain.promotion.presentation.dto.request.PromotionCreateRequest;
import org.example.pdnight.domain.promotion.presentation.dto.response.PromotionParticipantResponse;
import org.example.pdnight.domain.promotion.presentation.dto.response.PromotionResponse;
import org.example.pdnight.global.common.dto.ApiResponse;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    // 프로모션 조회
    @GetMapping("/promotions/{id}")
    public ResponseEntity<ApiResponse<PromotionResponse>> findPromotionById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("프로모션 조회 성공했습니다.", promotionService.findPromotionById(id))
        );
    }

    // 프로모션 리스트 조회
    @GetMapping("/promotions")
    public ResponseEntity<ApiResponse<PagedResponse<PromotionResponse>>> findPromotionById(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return ResponseEntity.ok(
                ApiResponse.ok("프로모션 리스트 조회 성공했습니다.", promotionService.findPromotionList(pageable))
        );
    }

    // 프로모션 참가 신청
    @PostMapping("/promotions/{id}/participants")
    public ResponseEntity<ApiResponse<Void>> addParticipant(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long userId = loginUser.getUserId();
        promotionService.addParticipant(id, userId);
        return ResponseEntity.ok(
                ApiResponse.ok("프로모션 참가 신청 성공했습니다.", null)
        );
    }

    // 내가 참가한 프로모션 조회
    @GetMapping("/my/participant-promotions")
    public ResponseEntity<ApiResponse<PagedResponse<PromotionResponse>>> getMyParticipantPromotions(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @PageableDefault() Pageable pageable
    ) {
        Long userId = loginUser.getUserId();
        PagedResponse<PromotionResponse> myParticipantPromotions = promotionService.findMyParticipantPromotions(userId, pageable);
        return ResponseEntity.ok(
                ApiResponse.ok("프로모션 목록 조회 성공했습니다.", myParticipantPromotions)
        );
    }

    // ------------------------------ Admin 컨트롤러 합치기 -----------------------------

    // 프로모션 생성
    @PostMapping("/admin/promotions")
    public ResponseEntity<ApiResponse<PromotionResponse>> createPromotion(
            @RequestBody PromotionCreateRequest request
    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("프로모션 생성 성공했습니다.", promotionService.createPromotion(request)));
    }

    // 프로모션 수정
    @PatchMapping("/admin/promotions/{id}")
    public ResponseEntity<ApiResponse<PromotionResponse>> updatePromotion(
            @PathVariable Long id,
            @RequestBody PromotionCreateRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("프로모션 수정 성공했습니다", promotionService.updatePromotion(id, request))
        );
    }

    // 프로모션 삭제
    @DeleteMapping("/admin/promotions/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePromotion(
            @PathVariable Long id
    ) {
        promotionService.deletePromotionById(id);
        return ResponseEntity.ok(
                ApiResponse.ok("프로모션 삭제 성공했습니다.", null)
        );
    }

    // 프로모션 참가 인원 리스트 조회
    @GetMapping("/admin/promotions/{id}/participants")
    public ResponseEntity<ApiResponse<PagedResponse<PromotionParticipantResponse>>> getPromotionParticipants(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return ResponseEntity.ok(
                ApiResponse.ok("프로모션 참가 인원 리스트 조회 성공했습니다.", promotionService.findPromotionParticipantList(id, pageable))
        );
    }

}
