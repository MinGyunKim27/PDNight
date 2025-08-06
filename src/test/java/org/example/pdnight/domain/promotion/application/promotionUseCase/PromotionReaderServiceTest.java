package org.example.pdnight.domain.promotion.application.promotionUseCase;

import java.util.List;
import java.util.Optional;
import org.assertj.core.util.Lists;
import org.example.pdnight.domain.promotion.domain.PromotionReader;
import org.example.pdnight.domain.promotion.domain.entity.Promotion;
import org.example.pdnight.domain.promotion.domain.entity.PromotionParticipant;
import org.example.pdnight.domain.promotion.presentation.dto.response.PromotionParticipantResponse;
import org.example.pdnight.domain.promotion.presentation.dto.response.PromotionResponse;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PromotionReaderServiceTest {
    @InjectMocks
    private PromotionReaderService promotionReaderService;

    @Mock
    private PromotionReader promotionReader;

    @Test
    @DisplayName("프로모션 조회")
    void 프로모션_조회(){
        LocalDateTime fixedDateTime = LocalDateTime.now().plusDays(1);
        Promotion promotion = Promotion.from("title", "content", 50, fixedDateTime, fixedDateTime.plusDays(10));
        ReflectionTestUtils.setField(promotion, "id", 1L);

        when(promotionReader.findById(1L)).thenReturn(Optional.of(promotion));

        PromotionResponse response = promotionReaderService.findPromotionById(1L);

        assert response != null;
        assert response.getId() == 1L;
        assert response.getTitle().equals("title");
        assert response.getContent().equals("content");
        assert response.getMaxParticipants() == 50;
        assert response.getPromotionStartDate().equals(fixedDateTime);
        assert response.getPromotionEndDate().equals(fixedDateTime.plusDays(10));
    }

    @Test
    @DisplayName("프로모션 리스트 조회")
    void 프로모션_리스트_조회 () {
        LocalDateTime fixedDateTime = LocalDateTime.now().plusDays(1);
        Promotion promotion1 = Promotion.from("title1", "content1", 50, fixedDateTime, fixedDateTime.plusDays(10));
        ReflectionTestUtils.setField(promotion1, "id", 1L);
        Promotion promotion2 = Promotion.from("title2", "content2", 50, fixedDateTime, fixedDateTime.plusDays(10));
        ReflectionTestUtils.setField(promotion2, "id", 2L);

        Pageable pageable = PageRequest.of(0, 10);
        List<Promotion> promotions = Lists.newArrayList(promotion1, promotion2);
        Page<Promotion> page = new PageImpl<>(promotions, pageable, promotions.size());

        when(promotionReader.findAllPromotion(pageable)).thenReturn(page);

        PagedResponse<PromotionResponse> result = promotionReaderService.findPromotionList(pageable);

        assert result != null;
        assert result.contents().get(0).getTitle().equals("title1");
        assert result.contents().get(1).getTitle().equals("title2");
    }

    @Test
    @DisplayName("프로모션 참가 신청 유저 목록 조회")
    void 프로모션_참가_신청_유저_목록(){
        Long promotionId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Promotion promotion = Promotion.from(
                "title", "content", 50, LocalDateTime.now(), LocalDateTime.now().plusDays(10)
        );
        User user = User.createTestUser(
                1L, "name", "name@email.com", "asdf1234!"
        );
        PromotionParticipant participant = PromotionParticipant.create(promotion, 1L);

        List<PromotionParticipant> participants = List.of(participant);
        Page<PromotionParticipant> page = new PageImpl<>(participants, pageable, participants.size());

        when(promotionReader.findById(promotionId)).thenReturn(Optional.of(promotion)); // 이 부분 추가
        when(promotionReader.findByPromotionWithUser(promotion, pageable)).thenReturn(page);

        PagedResponse<PromotionParticipantResponse> result = promotionReaderService.findPromotionParticipantList(promotionId, pageable);

        assertNotNull(result);
        assertEquals(1, result.contents().size());
        assertEquals(user.getId(), result.contents().get(0).getUser().get(0)); // getUserId()가 있을 경우

    }

    @Test
    @DisplayName("내가 신청한 프로모션 목록 조회")
    void 내가_신청한_프로모션_목록(){
        Promotion promotion1 = Promotion.from(
                "title1", "content1", 50, LocalDateTime.now(), LocalDateTime.now().plusDays(10)
        );
        Promotion promotion2 = Promotion.from(
                "title2", "content2", 50, LocalDateTime.now(), LocalDateTime.now().plusDays(10)
        );
        ReflectionTestUtils.setField(promotion1, "id", 1L);
        ReflectionTestUtils.setField(promotion2, "id", 2L);

        Pageable pageable = PageRequest.of(0, 10);

        List<Promotion> promotions = Lists.newArrayList(promotion1, promotion2);
        Page<Promotion> page = new PageImpl<>(promotions, pageable, promotions.size());
        when(promotionReader.findAllPromotion(pageable)).thenReturn(page);
        PagedResponse<PromotionResponse> result = promotionReaderService.findPromotionList(pageable);

        assertNotNull(result);
        assertEquals(2, result.contents().size());
        assertEquals(promotion1.getId(), result.contents().get(0).getId());
    }
}
