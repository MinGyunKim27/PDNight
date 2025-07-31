package org.example.pdnight.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pdnight.domain.auth.presentation.dto.request.LoginRequest;
import org.example.pdnight.domain.auth.presentation.dto.request.SignupRequest;
import org.example.pdnight.domain.auth.presentation.dto.request.WithdrawRequest;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.common.enums.UserRole;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.enums.Region;
import org.example.pdnight.domain.user.infra.userInfra.UserJpaRepository;
import org.example.pdnight.global.config.PasswordEncoder;
import org.example.pdnight.global.utils.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원 가입 성공 테스트")
    void signInSuccess() throws Exception {
        //given
        SignupRequest request = signInRequest("Signup");

        //when
        ResultActions perform = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value("SignupTest@example.com"));
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void loginSuccess() throws Exception {
        //given
        SignupRequest request = signInRequest("login");
        createUser(request);

        LoginRequest loginRequest = loginRequest("login");

        //when
        ResultActions perform = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").isNotEmpty());
    }

    @Test
    @DisplayName("필터 테스트 - 토큰이 없는 경우 회원 탈퇴 실패")
    void filterTestNoToken() throws Exception {
        //given
        WithdrawRequest withdrawRequest = withdrawRequest();

        //when
        mockMvc.perform(delete("/api/auth/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("JWT 토큰이 필요합니다."));
    }

    @Test
    @DisplayName("토큰이 있는 경우 회원 탈퇴 성공")
    void Withdraw() throws Exception {
        //given
        SignupRequest request = signInRequest("withDraw");
        User user = createUser(request);
        String token = jwtUtil.createToken(user.getId(), user.getRole(), user.getNickname());

        WithdrawRequest withdrawRequest = withdrawRequest();
        //when
        mockMvc.perform(delete("/api/auth/withdraw")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                //then
                .andExpect(status().isOk());
    }

    private SignupRequest signInRequest(String name) {
        return SignupRequest.builder()
                .email(name + "Test@example.com")
                .password("testPassword")
                .name("test")
                .gender(Gender.MALE)
                .age(20L)
                .jobCategory(JobCategory.BACK_END_DEVELOPER)
                .region(Region.PANGYO_DONG)
                .workLocation(Region.BAEKHYEON_DONG).build();
    }

    private LoginRequest loginRequest(String name) {
        return LoginRequest.builder()
                .email(name + "Test@example.com")
                .password("testPassword").build();
    }

    private WithdrawRequest withdrawRequest() {
        return WithdrawRequest.builder()
                .password("testPassword").build();
    }

    private User createUser(SignupRequest request) {
        String encode = passwordEncoder.encode(request.getPassword());
        User user = User.create(request.getEmail(),
                encode,
                UserRole.USER,
                request.getName(),
                request.getNickname(),
                request.getGender(),
                request.getAge(),
                request.getJobCategory(),
                request.getRegion(),
                request.getWorkLocation(),
                request.getComment());
        return userJpaRepository.save(user);
    }
}
