package org.example.pdnight.domain.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.transaction.Transactional;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        // 테스트용 사용자 DB에 저장
        User user = new User("test@example.com", "Test", BCrypt.withDefaults().hashToString(10, "password".toCharArray()));
        userRepository.save(user);
    }

    @Test
    public void 내_프로필_조회_성공() throws Exception {
        User user = userRepository.findAll().get(0);

        mockMvc.perform(get("/my/profile")
                        .principal(() -> user.getId().toString()))  // 인증된 사용자로 모킹
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value("Test"));
    }

    @Test
    public void 비밀번호_수정_성공() throws Exception {
        User user = userRepository.findAll().get(0);

        String json = """
            {
                "oldPassword": "password",
                "newPassword": "newpass123"
            }
            """;

        mockMvc.perform(patch("/my/password")
                        .principal(() -> user.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        // 비밀번호가 실제로 바뀌었는지 검증
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertNotEquals(user.getPassword(), updatedUser.getPassword());
    }
}
