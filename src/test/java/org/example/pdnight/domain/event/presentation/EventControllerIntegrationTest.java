package org.example.pdnight.domain.event.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pdnight.domain.auth.domain.AuthCommander;
import org.example.pdnight.domain.auth.domain.entity.Auth;
import org.example.pdnight.domain.auth.presentation.dto.request.SignupRequest;
import org.example.pdnight.domain.event.presentation.dto.request.EventCreateRequest;
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
class EventControllerIntegrationTest {

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
    @DisplayName("이벤트 생성 성공")
    void 이벤트_생성_성공() throws Exception {
        String token = login();
        ResultActions eventPerform = createEvent(token);

        //then
        eventPerform.andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value("Test Event"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이벤트 생성 성공했습니다."));
    }

    @Test
    @Order(2)
    @DisplayName("이벤트_단건_조회 성공")
    void 이벤트_단건_조회_성공() throws Exception {
        String token = login();

        //when
        ResultActions eventUpdatePerform = mockMvc.perform(get("/api/events/1")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                );

        //then
        eventUpdatePerform.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value("Test Event"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이벤트 조회 성공했습니다."));
    }

    @Test
    @Order(3)
    @DisplayName("이벤트 다건 조회 가능")
    void 이벤트_다건_조회_가능() throws Exception {
        String token = login();
        createEvent(token);
        createEvent(token);
        createEvent(token);

        //when
        ResultActions eventUpdatePerform = mockMvc.perform(get("/api/events")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        eventUpdatePerform.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이벤트 리스트 조회 성공했습니다."));
    }

    @Test
    @Order(4)
    @DisplayName("이벤트 수정 성공")
    void 이벤트_수정_성공() throws Exception {
        String token = login();

        EventCreateRequest request = EventCreateRequest.from(
                "Updated Event", "내용", 10,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        //when
        ResultActions eventUpdatePerform = mockMvc.perform(patch("/api/admin/events/1")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        eventUpdatePerform.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value("Updated Event"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이벤트 수정 성공했습니다"));
    }

    @Test
    @Order(5)
    @DisplayName("이벤트_삭제_성공")
    void 이벤트_삭제_성공() throws Exception {
        String token = login();

        //when
        ResultActions eventUpdatePerform = mockMvc.perform(delete("/api/admin/events/1")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                );

        //then
        eventUpdatePerform.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이벤트 삭제 성공했습니다."));
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

    private ResultActions createEvent(String token) throws Exception {
        EventCreateRequest request = EventCreateRequest.from(
                "Test Event", "내용", 10,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        //when
       return mockMvc.perform(post("/api/admin/events")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }
}

