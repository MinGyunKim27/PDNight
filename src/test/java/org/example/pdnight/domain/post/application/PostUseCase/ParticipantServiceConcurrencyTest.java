package org.example.pdnight.domain.post.application.PostUseCase;

import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.domain.post.PostCommander;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.enums.JobCategory;
import org.example.pdnight.global.common.exception.BaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class ParticipantServiceConcurrencyTest {

    @Autowired
    PostCommander postCommander;

    @Autowired
    PostCommanderService postCommanderService;

    private Post testPost;

    @BeforeEach
    void setUp() {
        // 선착순 포스트 생성
        testPost = Post.createPost(
                500L,
                "테스트 제목",
                LocalDateTime.now().plusDays(1),
                "공개 내용",
                50,
                Gender.ALL,
                JobCategory.ALL,
                AgeLimit.ALL,
                true
        );
        testPost = postCommander.save(testPost);
    }

    //테스트 참가 신청 동시성 테스트
    @Test
    void testConcurrentApplyParticipant() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(200);
        List<Future<String>> futures = new ArrayList<>();

        for (long i = 0L ; i < 200L; i++ ) {
            Long userId = i;
            futures.add(executor.submit(() -> {
                try {
                    postCommanderService.applyParticipant(userId, 20L, Gender.MALE, JobCategory.BACK_END_DEVELOPER, testPost.getId());
                    return "SUCCESS:" + userId;
                } catch (BaseException e) {
                    return "FAIL:" + userId + ":" + e.getMessage();
                } finally {
                    latch.countDown();
                }
            }));
        }

        latch.await();
        executor.shutdown();
        // 결과 출력
        long successCount = futures.stream()
                .map(f -> {
                    try {
                        return f.get();
                    } catch (Exception e) {
                        return "ERROR";
                    }
                })
                .filter(res -> res.startsWith("SUCCESS"))
                .count();

        System.out.println("성공 참가자 수: " + successCount);

        // 저장소에서 최신 상태 조회

        Post postAfter = postCommander.findByIdAndStatus(testPost.getId(), PostStatus.CONFIRMED)
                .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));

        long acceptedCount = postAfter.getPostParticipants().stream()
                .filter(p -> p.getStatus() == JoinStatus.ACCEPTED)
                .count();

        assertEquals(postAfter.getMaxParticipants().intValue(), acceptedCount);
        assertEquals(successCount, acceptedCount);
    }

    @Test
    void testConcurrentRejectAndCancel() throws InterruptedException {
        // 먼저 신청 10명
        for (long i = 0L ; i < 10L; i++ ) {
            Long userId = i;
            postCommanderService.applyParticipant(userId, 20L, Gender.MALE, JobCategory.BACK_END_DEVELOPER, testPost.getId());
        }

        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(20);

        // 게시자 (authorUser)와 참가자(user) 각각 동시에 거절, 취소 시도
        List<Future<String>> futures = new ArrayList<>();

        // 참가자 취소 요청 (pending 상태라고 가정)
        for (long i = 0L ; i < 20L; i++ ) {
            Long userId = i;
            futures.add(executor.submit(() -> {
                try {
                    postCommanderService.deleteParticipant(userId, testPost.getId());
                    return "CANCEL_SUCCESS:" + userId;
                } catch (BaseException e) {
                    return "CANCEL_FAIL:" + userId + ":" + e.getMessage();
                } finally {
                    latch.countDown();
                }
            }));
        }

        // 게시자 거절 요청 (pending -> rejected)
        for (long i = 0L ; i < 20L; i++ ) {
            Long userId = i;
            futures.add(executor.submit(() -> {
                try {
                    postCommanderService.changeStatusParticipant(500L, userId, testPost.getId(), "REJECTED");
                    return "REJECT_SUCCESS:" + userId;
                } catch (BaseException e) {
                    return "REJECT_FAIL:" + userId + ":" + e.getMessage();
                } finally {
                    latch.countDown();
                }
            }));
        }

        latch.await();
        executor.shutdown();

        // 결과 출력
        for (Future<String> f : futures) {
            try {
                System.out.println(f.get());
            } catch (Exception e) {
                System.out.println("ERROR");
            }
        }
    }
}
