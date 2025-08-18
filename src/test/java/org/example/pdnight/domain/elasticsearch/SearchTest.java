package org.example.pdnight.domain.elasticsearch;

import org.example.pdnight.domain.post.application.PostUseCase.PostReaderESService;
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
import org.springframework.transaction.annotation.Transactional;

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
    private PostReaderESService postReaderESService;

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
    @Transactional("transactionManager")
    void mysqlSearch() {
        long start = System.currentTimeMillis();
        Pageable pageable = PageRequest.of(1, 10);
        postReader.findPostsBySearch(pageable, 4, null, null, Gender.ALL);
        long end = System.currentTimeMillis();

        System.out.println("MySQL 총 소요시간(ms): " + (end - start));


        long startES = System.currentTimeMillis();
        postReaderESService.getPostDtosBySearchES(pageable, 4, null, null, Gender.ALL);
        long endES = System.currentTimeMillis();

        System.out.println("ElasticSearch 총 소요시간(ms): " + (endES - startES));
    }

}