package org.example.pdnight.domain.auth.presentaion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pdnight.domain.auth.domain.AuthCommander;
import org.example.pdnight.domain.auth.domain.entity.Auth;
import org.example.pdnight.domain.auth.presentation.dto.request.LoginRequest;
import org.example.pdnight.domain.auth.presentation.dto.request.SignupRequest;
import org.example.pdnight.domain.auth.presentation.dto.request.WithdrawRequest;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.userDomain.UserCommander;
import org.example.pdnight.global.common.enums.JobCategory;
import org.example.pdnight.global.common.enums.UserRole;
import org.example.pdnight.global.config.PasswordEncoder;
import org.example.pdnight.global.utils.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
@EmbeddedKafka(
        count = 3,
        ports = {10000, 10001, 10002},
        topics = {"test-topic", "test-topic.DLT"},
        brokerProperties = {
                "auto.create.topics.enable=true",
                "offsets.topic.replication.factor=1",
                "transaction.state.log.replication.factor=1",
                "transaction.state.log.min.isr=1"
        }
)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthCommander authCommander;

    @Autowired
    private UserCommander userCommander;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("회원 가입 성공 테스트")
    @Order(1)
    void signInSuccess() throws Exception {

        //given
        String email = "test@test.com";
        String name = "name";
        String password = "password";
        SignupRequest request = SignupRequest.builder()
                .email(email).nickname(name).name(name).password(password)
                .gender(Gender.MALE).age(25L).jobCategory(JobCategory.BACK_END_DEVELOPER)
                .build();

        //when
        ResultActions perform = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.message").value("회원가입 되었습니다."));
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    @Order(2)
    void loginSuccess() throws Exception {
        //given
        String email = "test@test.com";
        String password = "password";

        LoginRequest loginRequest = LoginRequest.builder()
                .email(email).password(password)
                .build();

        //when
        ResultActions perform = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.message").value("로그인 되었습니다."));

    }

    @Test
    @DisplayName("토큰이 있는 경우 회원 탈퇴 성공")
    @Order(3)
    void Withdraw() throws Exception {
        //given
        Long userId = 1L;
        String password = "password";
        Auth auth = authCommander.findById(userId).orElseThrow();
        User user = userCommander.findById(userId).orElseThrow();

        String token = jwtUtil.createToken(
                auth.getId(), auth.getRole(), user.getNickname(),
                user.getAge(), user.getGender(), user.getJobCategory());

        WithdrawRequest withdrawRequest = WithdrawRequest.builder()
                .password(password)
                .build();

        //when
        ResultActions perform = mockMvc.perform(delete("/api/auth/withdraw")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdrawRequest)));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원탈퇴 되었습니다."));
    }

}
