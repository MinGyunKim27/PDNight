package org.example.pdnight.domain.user.integrationTest;

import jakarta.transaction.Transactional;
import org.example.pdnight.domain.user.application.userUseCase.UserCommanderService;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.userDomain.UserCommander;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class UserFollowTest {
    @Autowired
    private UserCommander userCommander;

    @Autowired
    UserCommanderService userCommanderService;

    @Test
    @Transactional
    void follow_정상_팔로우_성공() {
        // given
        User userA = User.createTestUser(1L,"테스트","email@email.com","hasedPassword");
        userA.setId(null);
        userCommander.save(userA);
        User userB = User.createTestUser(1L,"테스트2","email@email.com","hasedPassword");;
        userB.setId(null);
        userCommander.save(userB);

        // when
        userCommanderService.follow(userB.getId(), userA.getId());

        // then
        assertThat(userA.getFollowedOther()).hasSize(1);
        assertThat(userB.getFollowingMe()).hasSize(1);
    }

    @Test
    @Transactional
    void follow_정상_언팔로우_성공() {
        // given
        User userA = User.createTestUser(1L,"테스트","email@email.com","hasedPassword");
        userA.setId(null);
        userCommander.save(userA);
        User userB = User.createTestUser(1L,"테스트2","email@email.com","hasedPassword");;
        userB.setId(null);
        userCommander.save(userB);

        // when
        userCommanderService.follow(userB.getId(), userA.getId());

        userCommanderService.unfollow(userB.getId(), userA.getId());

        // then
        assertThat(userA.getFollowedOther()).hasSize(0);
        assertThat(userB.getFollowingMe()).hasSize(0);
    }

}
