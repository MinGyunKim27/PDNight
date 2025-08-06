package org.example.pdnight.domain.promotion.application.promotionUseCase;

import org.example.pdnight.domain.promotion.domain.PromotionCommander;
import org.example.pdnight.domain.promotion.domain.PromotionReader;
import org.example.pdnight.domain.promotion.domain.entity.Promotion;
import org.example.pdnight.global.common.exception.BaseException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PromotionConcurrencyTest {

    @Autowired
    private PromotionCommanderService promotionCommanderService;

    @Autowired
    private PromotionReader promotionReader;

    @Autowired
    private PromotionCommander promotionCommander;

    @BeforeEach
    void setUp() {
        // 이벤트 초기화
        Promotion promotion = Promotion.from(
                "동시성 테스트 이벤트",
                "설명",
                10, // 정원 10명
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );
        promotionCommander.save(promotion);
    }

    @Test
    void 동시에20명이신청하면10명만성공한다() throws InterruptedException {
        // given
        int threadCount = 20;
        // 동시에 실행할 스레드 풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        // 모든 스레드가 끝날 때까지 기다리기 위한 동기화 도구
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 테스트 대상 이벤트 ID 추출
        Long promotionId = promotionReader.findAllPromotion(PageRequest.of(0, 1))
                .getContent().get(0).getId();

        // 동시성 결과 저장용
        List<Long> successUsers = Collections.synchronizedList(new ArrayList<>());
        Map<Long, String> failedUsers = new ConcurrentHashMap<>();

        // when - 20명의 유저가 동시에 신청 시도
        for (int i = 0; i < threadCount; i++) {
            final Long userId = Long.valueOf(i + 2);
            // 각 스레드에서 이벤트 신청 시도
            executorService.submit(() -> {
                try {
                    promotionCommanderService.addParticipant(promotionId, userId);
                    successUsers.add(userId);
                } catch (BaseException e) {
                    failedUsers.put(userId, e.getMessage());
                } catch (Exception e) {
                    failedUsers.put(userId, "Unknown error: " + e.getMessage());
                } finally {
                    // 각 스레드 작업 완료 후 latch 감소
                    latch.countDown();
                }
            });
        }

        // 모든 스레드 종료 대기
        latch.await();
        executorService.shutdown();

        // then - 결과 확인
        System.out.println("--------- 테스트 결과 ----------");
        System.out.println("성공한 유저 수: " + successUsers.size());
        System.out.println("실패한 유저 수: " + failedUsers.size());
        System.out.println("성공한 유저 목록: " + successUsers);
        System.out.println("실패한 유저 상세: " + failedUsers);

        assertThat(successUsers.size()).isEqualTo(10);
        assertThat(failedUsers.size()).isEqualTo(10);
    }

}
