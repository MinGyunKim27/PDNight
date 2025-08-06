package org.example.pdnight.domain.promotion.application.promotionUseCase;

import org.example.pdnight.domain.promotion.domain.PromotionCommander;
import org.example.pdnight.domain.promotion.domain.PromotionReader;
import org.example.pdnight.domain.promotion.domain.entity.Promotion;
import org.example.pdnight.domain.promotion.presentation.dto.request.PromotionCreateRequest;
import org.example.pdnight.domain.promotion.presentation.dto.response.PromotionResponse;
import org.example.pdnight.global.common.exception.BaseException;
import org.junit.jupiter.api.DisplayName;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PromotionCommanderServiceTest {

    @InjectMocks
    private PromotionCommanderService promotionCommanderService;

    @Mock
    private PromotionCommander promotionCommander;

    @Mock
    private PromotionReader promotionReader;

    @Test
    @DisplayName("프로모션 생성 확인 테스트")
    void 프로모션_생성_성공() {
        LocalDateTime fixedDateTime = LocalDateTime.now().plusDays(1);

        PromotionCreateRequest request = PromotionCreateRequest.from(
                "title", "content", 50, fixedDateTime, fixedDateTime.plusDays(10)
        );
        Promotion promotion = Promotion.from(
                request.getTitle(),
                request.getContent(),
                request.getMaxParticipants(),
                request.getPromotionStartDate(),
                request.getPromotionEndDate()
        );
        // 테스트용 id 직접 세팅
        ReflectionTestUtils.setField(promotion, "id", 1L);

        when(promotionCommander.save(any(Promotion.class))).thenReturn(promotion);

        PromotionResponse response = promotionCommanderService.createPromotion(request);

        assertThat(response.getTitle()).isEqualTo("title");
        assertThat(response.getContent()).isEqualTo("content");
        assertThat(response.getMaxParticipants()).isEqualTo(50);
        assertThat(response.getPromotionStartDate()).isEqualTo(fixedDateTime);
        assertThat(response.getPromotionEndDate()).isEqualTo(fixedDateTime.plusDays(10));
    }

    @Test
    @DisplayName("프로모션 생성 실패 - 이상한 날짜")
    void 프로모션_생성_실패_날짜_오류(){
        LocalDateTime fixedDateTime = LocalDateTime.now().minusYears(1);
        PromotionCreateRequest request = PromotionCreateRequest.from(
                "title", "content", 50, fixedDateTime, fixedDateTime.plusDays(10)
        );

        assertThatThrownBy(() -> promotionCommanderService.createPromotion(request))
                .isInstanceOf(BaseException.class)
                .hasMessage("프로모션 일자가 잘못되었습니다.");
    }

    @Test
    @DisplayName("프로모션 생성 실패 - 이상한 정원")
    void 프로모션_생성_실패_이상한_정원(){
        LocalDateTime fixedDateTime = LocalDateTime.now().plusDays(1);

        PromotionCreateRequest request = PromotionCreateRequest.from(
                "title", "content", 0, fixedDateTime, fixedDateTime.plusDays(10)
        );

        assertThatThrownBy(() -> promotionCommanderService.createPromotion(request))
                .isInstanceOf(BaseException.class)
                .hasMessage("프로모션 정원은 1명 이상이어야 합니다.");
    }

    @Test
    @DisplayName("프로모션 수정 성공")
    void 프로모션_수정_성공(){
        LocalDateTime fixedDateTime = LocalDateTime.now().plusDays(1);

        Promotion promotion = Promotion.from(
                "title", "content", 50, fixedDateTime, fixedDateTime.plusDays(10)
        );
        ReflectionTestUtils.setField(promotion, "id", 1L);

        PromotionCreateRequest request = PromotionCreateRequest.from(
                "updated title", "updated content", 100, fixedDateTime, fixedDateTime.plusDays(10)
        );

        when(promotionCommander.save(any(Promotion.class))).thenReturn(promotion);
        when(promotionReader.findById(1L)).thenReturn(Optional.of(promotion));

        PromotionResponse response = promotionCommanderService.updatePromotion(1L, request);

        assertThat(response.getTitle()).isEqualTo("updated title");
        assertThat(response.getContent()).isEqualTo("updated content");
        assertThat(response.getMaxParticipants()).isEqualTo(100);
    }

    @Test
    @DisplayName("프로모션 삭제 성공")
    void 프로모션_삭제_성공(){
        LocalDateTime fixedDateTime = LocalDateTime.now().plusDays(1);

        Promotion promotion = Promotion.from(
                "title", "content", 50, fixedDateTime, fixedDateTime.plusDays(10)
        );
        ReflectionTestUtils.setField(promotion, "id", 1L);

        when(promotionReader.findById(1L)).thenReturn(Optional.of(promotion));

        promotionCommanderService.deletePromotionById(1L);

        verify(promotionCommander, times(1)).delete(promotion);
    }

    @Test
    @DisplayName("프로모션 참가 신청 성공")
    void 프로모션_참가_신청_성공(){
        LocalDateTime fixedDateTime = LocalDateTime.now().plusDays(1);

        Promotion promotion = Promotion.from(
                "title", "content", 50, fixedDateTime, fixedDateTime.plusDays(10)
        );
        ReflectionTestUtils.setField(promotion, "id", 1L);

        when(promotionReader.findById(1L)).thenReturn(Optional.of(promotion));
        when(promotionReader.getPromotionParticipantByPromotionId(1L)).thenReturn(1L);

        promotionCommanderService.addParticipant(1L, 1L);

        assertThat(promotion.getPromotionParticipants().get(0).getUserId()).isEqualTo(1L);
    }

    @Test
    void createPromotion_참가인원_0명_예외() {
        LocalDateTime fixedDateTime = LocalDateTime.now().plusDays(1);

        PromotionCreateRequest request = PromotionCreateRequest.from(
                "제목", "내용", 0,fixedDateTime, fixedDateTime.plusDays(1)
        );

        assertThatThrownBy(() -> promotionCommanderService.createPromotion(request))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.PROMOTION_INVALID_PARTICIPANT.getMessage());
    }

    @Test
    @DisplayName("프로모션 참가 - 이미 참가한 유저 예외")
    void 프로모션_참가_이미_참가한_유저_예외() {
        // given
        Long promotionId = 1L;
        Long userId = 10L;

        when(promotionReader.existsPromotionByIdAndUserId(promotionId, userId)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> promotionCommanderService.addParticipant(promotionId, userId))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.PROMOTION_ALREADY_PENDING.getMessage());
    }

    @Test
    @DisplayName("프로모션 참가 - 정원 초과")
    void 프로모션_참가_정원_초과_예외() {
        LocalDateTime fixedDateTime = LocalDateTime.now().plusDays(1);

        // given
        Long promotionId = 1L;
        Long userId = 10L;

        Promotion promotion = Promotion.from("제목", "내용", 2, fixedDateTime, fixedDateTime.plusDays(1));

        when(promotionReader.existsPromotionByIdAndUserId(promotionId, userId)).thenReturn(false);
        when(promotionReader.findById(promotionId)).thenReturn(Optional.of(promotion));
        when(promotionReader.getPromotionParticipantByPromotionId(promotionId)).thenReturn(2L); // 이미 정원 초과

        // when & then
        assertThatThrownBy(() -> promotionCommanderService.addParticipant(promotionId, userId))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.PROMOTION_PARTICIPANT_FULL.getMessage());
    }
}
