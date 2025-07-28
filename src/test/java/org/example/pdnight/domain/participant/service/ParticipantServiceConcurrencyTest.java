package org.example.pdnight.domain.participant.service;

import jakarta.transaction.Transactional;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.common.enums.JoinStatus;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.participant.repository.ParticipantRepository;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.post.repository.PostRepository;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@Transactional
public class ParticipantServiceConcurrencyTest {

    @Autowired
    ParticipantService participantService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    ParticipantRepository participantRepository;

    private Post testPost;
    private List<User> testUsers;
    private User authorUser;

    @BeforeEach
    void setUp() {
        // 게시글 작성자 및 선착순 포스트 생성
        authorUser = userRepository.findById(201L).orElseThrow();
        testPost = postRepository.save(Post.createPost(
                authorUser,
                "테스트 제목",
                LocalDateTime.now().plusDays(1),
                "공개 내용",
                "비공개 내용",
                50,
                Gender.ALL,
                JobCategory.ALL,
                AgeLimit.ALL,
                true,
                PostStatus.OPEN
        ));
    }

    @Test
    void testConcurrentApplyParticipant() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(testUsers.size());

        List<Future<String>> futures = new ArrayList<>();

        for (User user : testUsers) {
            futures.add(executor.submit(() -> {
                try {
                    participantService.applyParticipant(user.getId(), testPost.getId());
                    return "SUCCESS:" + user.getId();
                } catch (BaseException e) {
                    return "FAIL:" + user.getId() + ":" + e.getMessage();
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

        // 최대 참가 인원 초과 금지 검증
        int acceptedCount = participantRepository.countByPostAndStatus(testPost, JoinStatus.ACCEPTED);
        assertEquals(testPost.getMaxParticipants().intValue(), acceptedCount);

        // 성공 참가자 수와 DB 참가자 수는 같아야 함
        assertEquals(successCount, acceptedCount);
    }

    @Test
    void testConcurrentRejectAndCancel() throws InterruptedException {
        // 먼저 신청 10명
        List<User> participants = testUsers.subList(0, 10);
        for (User u : participants) {
            participantService.applyParticipant(u.getId(), testPost.getId());
        }

        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(20);

        // 게시자 (authorUser)와 참가자(user) 각각 동시에 거절, 취소 시도
        List<Future<String>> futures = new ArrayList<>();

        // 참가자 취소 요청 (pending 상태라고 가정)
        for (User participant : participants) {
            futures.add(executor.submit(() -> {
                try {
                    participantService.deleteParticipant(participant.getId(), testPost.getId());
                    return "CANCEL_SUCCESS:" + participant.getId();
                } catch (BaseException e) {
                    return "CANCEL_FAIL:" + participant.getId() + ":" + e.getMessage();
                } finally {
                    latch.countDown();
                }
            }));
        }

        // 게시자 거절 요청 (pending -> rejected)
        for (User participant : participants) {
            futures.add(executor.submit(() -> {
                try {
                    participantService.changeStatusParticipant(authorUser.getId(), participant.getId(), testPost.getId(), "REJECTED");
                    return "REJECT_SUCCESS:" + participant.getId();
                } catch (BaseException e) {
                    return "REJECT_FAIL:" + participant.getId() + ":" + e.getMessage();
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
