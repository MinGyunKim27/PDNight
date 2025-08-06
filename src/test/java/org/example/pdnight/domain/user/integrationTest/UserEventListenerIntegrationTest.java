package org.example.pdnight.domain.user.integrationTest;

import jakarta.transaction.Transactional;
import org.example.pdnight.domain.auth.presentation.dto.request.SignupRequest;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.userDomain.UserReader;
import org.example.pdnight.global.common.enums.JobCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest
//@ActiveProfiles("test")
//class UserEventListenerIntegrationTest {
//
//    @Autowired
//    private TestEventPublisher testEventPublisher;
//
//    @Autowired
//    private UserReader userReader;
//
//    private static final String TEST_NAME = "James";
//
//    @Test
//    void 회원가입_이벤트_정상_동작() {
//        // given
//        SignupRequest request = SignupRequest.builder()
//                .email("test@example.com")
//                .password("password123")
//                .name(TEST_NAME)
//                .nickname("길동이")
//                .gender(Gender.MALE)
//                .age(30L)
//                .jobCategory(JobCategory.AI_RESEARCHER)
//                .build();
//
//        // when
//        testEventPublisher.publishSignUpEvent(request); // 트랜잭션 안에서 이벤트 발행
//
//        // then
//        List<User> users = userReader.searchUsers(TEST_NAME, PageRequest.of(0, 10)).getContent();
//        assertThat(users).hasSize(1);
//    }
//
//    @TestConfiguration
//    static class TestConfig {
//        @Bean
//        public TestEventPublisher testEventPublisher(ApplicationEventPublisher publisher) {
//            return new TestEventPublisher(publisher);
//        }
//    }

//    static class TestEventPublisher {
//
//        private final ApplicationEventPublisher publisher;
//
//        public TestEventPublisher(ApplicationEventPublisher publisher) {
//            this.publisher = publisher;
//        }
//
//        @Transactional
//        public void publishSignUpEvent(SignupRequest request) {
//            publisher.publishEvent(UserSignedUpEvent.of(1L, request));
//        }
//    }
// }