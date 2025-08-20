package org.example.pdnight.domain.elasticsearch;

import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.domain.post.PostDocument;
import org.example.pdnight.domain.post.domain.post.PostReader;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.post.infra.post.PostESRepository;
import org.example.pdnight.domain.post.infra.post.PostJpaRepository;
import org.example.pdnight.global.common.enums.JobCategory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SearchTest {

    @Autowired
    private PostReader postReader;

    @Autowired
    private PostJpaRepository postJpaRepository;

    @Autowired
    private PostESRepository postESRepository;

    @BeforeAll
    void setUp() {
        List<Post> postList = new ArrayList<>();
        List<PostDocument> postDocumentList = new ArrayList<>();
        for (Long i = 0L; i < 100000; i++) {
            int num = (int) (Math.random()*3);
            Gender gender;
            if(num == 0)  {
                gender = Gender.ALL;
            }
            else if(num == 1) {
                gender = Gender.MALE;
            }
            else {
                gender = Gender.FEMALE;
            }
            int maxParticipant = (int) (Math.random() * 4) + 1;
            postList.add(Post.createPost(
                    i,
                    "title" + i,
                    LocalDateTime.now(),
                    "",
                    maxParticipant,
                    gender,
                    JobCategory.ALL,
                    AgeLimit.ALL,
                    false,
                    null));

            postDocumentList.add(PostDocument.createPostDocument(
                    i,
                    i,
                    "title" + i,
                    LocalDateTime.now(),
                    "",
                    PostStatus.OPEN,
                    maxParticipant,
                    gender,
                    JobCategory.ALL,
                    AgeLimit.ALL,
                    false,
                    null,
                    new ArrayList<>(), null, false, null,
                    null));
        }
        postJpaRepository.saveAll(postList);
        postESRepository.saveAll(postDocumentList);
    }

    @Test
    @DisplayName("MYSQL 데이터 조회")
    void mysqlSearch() {
        long start = System.currentTimeMillis();
        Pageable pageable = PageRequest.of(1, 10);
        postReader.findPostsBySearch(pageable, 4, null, null, Gender.ALL);
        long end = System.currentTimeMillis();

        System.out.println("MySQL 총 소요시간(ms): " + (end - start));


        long startES = System.currentTimeMillis();
        postReader.findPostsBySearchES(pageable, 4, null, null, Gender.ALL);
        long endES = System.currentTimeMillis();

        System.out.println("ElasticSearch 총 소요시간(ms): " + (endES - startES));
    }

    @Test
    @DisplayName("성능 벤치마크 (평균값 출력)")
    void benchmark() {
        double[] shallow = runBenchmark("0 ~ 50 페이지", 0, 50, false);

        double[] deep100 = runBenchmark("100 ~ 110 페이지", 100, 110, false);

        double[] deep500 = runBenchmark("500 ~ 510 페이지)", 500, 510, false);

        double[] random = runBenchmark("조건 랜덤", 10, 20, true);

        // 결과 요약 출력
        System.out.println("\n===== 성능 결과 요약 (평균 ms) =====");
        System.out.printf("0 ~ 50 페이지: MySQL=%.2f ms, ES=%.2f ms%n", shallow[0], shallow[1]);
        System.out.printf("100 ~ 110 페이지: MySQL=%.2f ms, ES=%.2f ms%n", deep100[0], deep100[1]);
        System.out.printf("500 ~ 510 페이지: MySQL=%.2f ms, ES=%.2f ms%n", deep500[0], deep500[1]);
        System.out.printf("조건 랜덤(20회): MySQL=%.2f ms, ES=%.2f ms%n", random[0], random[1]);
    }

    private double[] runBenchmark(String label, int startPage, int endPage, boolean randomCond) {
        long totalMySQL = 0;
        long totalES = 0;
        int count = 0;

        for (int page = startPage; page <= endPage; page++) {
            Pageable pageable = PageRequest.of(page, 10);

            int maxParticipant;
            Gender gender;

            if (randomCond) {
                maxParticipant = (int) (Math.random() * 4) + 1;
                gender = Gender.values()[(int) (Math.random() * Gender.values().length)];
            } else {
                maxParticipant = 4;
                gender = Gender.ALL;
            }

            long start = System.currentTimeMillis();
            postReader.findPostsBySearch(pageable, maxParticipant, null, null, gender);
            totalMySQL += (System.currentTimeMillis() - start);

            long startES = System.currentTimeMillis();
            postReader.findPostsBySearchES(pageable, maxParticipant, null, null, gender);
            totalES += (System.currentTimeMillis() - startES);

            count++;
        }

        double avgMySQL = totalMySQL / (double) count;
        double avgES = totalES / (double) count;

        System.out.printf("[%s] MySQL 평균=%.2f ms, ES 평균=%.2f ms%n", label, avgMySQL, avgES);
        return new double[]{avgMySQL, avgES};
    }
}
