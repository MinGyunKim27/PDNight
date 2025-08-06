package org.example.pdnight.domain.promotion.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.example.pdnight.domain.auth.domain.AuthCommander;
import org.example.pdnight.domain.auth.domain.entity.Auth;
import org.example.pdnight.domain.auth.presentation.dto.request.SignupRequest;
import org.example.pdnight.domain.promotion.presentation.dto.request.PromotionCreateRequest;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.userDomain.UserCommander;
import org.example.pdnight.global.common.enums.JobCategory;
import org.example.pdnight.global.common.enums.UserRole;
import org.example.pdnight.global.config.PasswordEncoder;
import org.example.pdnight.global.utils.JwtUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PromotionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthCommander authCommander;

    @Autowired
    private UserCommander userCommander;

    private String email = "test@test.com";
    private String name = "name";
    private String password = "password";

    @Test
    @Order(1)
    @DisplayName("프로모션 생성 성공")
    void 프로모션생성단건조회성공() throws Exception {
        String token = login();
        ResultActions promotionPerform = createPromotion(token);

        //then
        promotionPerform.andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value("Test Promotion"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("프로모션 생성 성공했습니다."));
        String content = promotionPerform.andReturn().getResponse().getContentAsString();
        int promotionId = JsonPath.read(content, "$.data.id");

        //when
        ResultActions promotionUpdatePerform = mockMvc.perform(get("/api/promotions/" + promotionId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        promotionUpdatePerform.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value("Test Promotion"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("프로모션 조회 성공했습니다."));
    }

    @Test
    @Order(3)
    @DisplayName("프로모션 다건 조회 가능")
    void 프로모션_다건_조회_가능() throws Exception {
        String token = login();
        createPromotion(token);
        createPromotion(token);
        createPromotion(token);

        //when
        ResultActions promotionUpdatePerform = mockMvc.perform(get("/api/promotions")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        promotionUpdatePerform.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("프로모션 리스트 조회 성공했습니다."));
    }

    @Test
    @Order(4)
    @DisplayName("프로모션 수정 성공")
    void 프로모션_수정_성공() throws Exception {
        String token = login();

        PromotionCreateRequest request = PromotionCreateRequest.from(
                "Updated Event", "내용", 10,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        //when
        ResultActions promotionUpdatePerform = mockMvc.perform(patch("/api/admin/promotions/1")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        promotionUpdatePerform.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value("Updated Event"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("프로모션 수정 성공했습니다"));
    }

    @Test
    @Order(5)
    @DisplayName("프로모션_삭제_성공")
    void 프로모션_삭제_성공() throws Exception {
        String token = login();

        //when
        ResultActions promotionUpdatePerform = mockMvc.perform(delete("/api/admin/promotions/1")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                );

        //then
        promotionUpdatePerform.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("프로모션 삭제 성공했습니다."));
    }

    private Auth createAuth(SignupRequest request) {
        String encode = passwordEncoder.encode(request.getPassword());
        Auth auth = Auth.create(
                request.getEmail(),
                encode,
                UserRole.ADMIN
        );
        return authCommander.save(auth);
    }

    private User createUser(SignupRequest request) {
        User user = User.fromUserSignUpEvent(request);
        return userCommander.save(user);
    }

    private String login() throws Exception {
        SignupRequest signupRequest = SignupRequest.builder()
                .email(email).nickname(name).name(name).password(password)
                .gender(Gender.MALE).age(25L).jobCategory(JobCategory.BACK_END_DEVELOPER)
                .build();

        Auth auth = createAuth(signupRequest);
        User user = createUser(signupRequest);

        return jwtUtil.createToken(
                auth.getId(), auth.getRole(), user.getNickname(),
                user.getAge(), user.getGender(), user.getJobCategory());
    }

    private ResultActions createPromotion(String token) throws Exception {
        PromotionCreateRequest request = PromotionCreateRequest.from(
                "Test Promotion", "내용", 10,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        //when
       return mockMvc.perform(post("/api/admin/promotions")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }
}
