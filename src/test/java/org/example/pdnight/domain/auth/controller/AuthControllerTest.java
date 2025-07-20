package org.example.pdnight.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pdnight.domain.auth.dto.request.LoginRequestDto;
import org.example.pdnight.domain.auth.dto.request.SignInRequestDto;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.user.enums.Region;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Rollback
    @DisplayName("회원 가입 성공 테스트")
    void signInSuccess() throws Exception {
        //given
        SignInRequestDto request = signInRequest("SignIn");

        //when
        ResultActions perform = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value("SignInTest@example.com"));
    }

    @Test
    @Rollback
    @DisplayName("로그인 성공 테스트")
    void loginSuccess() throws Exception {
        //given
        SignInRequestDto request = signInRequest("login");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        LoginRequestDto loginRequest = LoginRequestDto.builder()
                .email("loginTest@example.com")
                .password("testPassword").build();

        //when
        ResultActions perform = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").isNotEmpty());
    }

    private SignInRequestDto signInRequest(String name) {
        return SignInRequestDto.builder()
                .email(name + "Test@example.com")
                .password("testPassword")
                .name("test")
                .gender(Gender.MALE)
                .age(20L)
                .jobCategory(JobCategory.BACK_END_DEVELOPER)
                .region(Region.PANGYO_DONG)
                .workLocation(Region.BAEKHYEON_DONG).build();
    }
}