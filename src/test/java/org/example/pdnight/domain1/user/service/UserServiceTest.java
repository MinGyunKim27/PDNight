package org.example.pdnight.domain1.user.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.example.pdnight.global.common.exception.BaseException;
import org.example.pdnight.domain1.hobby.entity.Hobby;
import org.example.pdnight.domain1.hobby.repository.HobbyRepositoryQuery;
import org.example.pdnight.domain1.techStack.entity.TechStack;
import org.example.pdnight.domain1.techStack.repository.TechStackRepositoryQuery;
import org.example.pdnight.domain1.user.dto.request.UserPasswordUpdateRequest;
import org.example.pdnight.domain1.user.dto.request.UserUpdateRequest;
import org.example.pdnight.domain1.user.dto.response.UserEvaluationResponse;
import org.example.pdnight.domain1.user.dto.response.UserResponseDto;
import org.example.pdnight.domain1.user.entity.User;
import org.example.pdnight.domain1.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HobbyRepositoryQuery hobbyRepositoryQuery;
    @Mock
    private TechStackRepositoryQuery techStackRepositoryQuery;

    @InjectMocks
    private UserService userService;

    @Test
    void 내_프로필_조회_성공() {
        // given
        Long userId = 1L;
        User user = mock();

        when(user.getName()).thenReturn("Test");
        when(user.getId()).thenReturn(userId);
        when(userRepository.findByIdWithInfo(userId)).thenReturn(Optional.of(user));

        // when
        UserResponseDto result = userService.getMyProfile(userId);

        // then
        assertEquals(userId, result.getId());
        assertEquals("Test", result.getName());
    }

    @Test
    void 내_프로필_조회_실패_유저없음() {
        // given
        Long userId = 1L;
        when(userRepository.findByIdWithInfo(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(BaseException.class, () -> userService.getMyProfile(userId));
    }

    @Test
    void 내_프로필_수정_성공() {
        // given
        Long userId = 1L;
        Long hobbyId = 1L;
        Long techStackId = 1L;

        User user = User.createTestUser(userId, "Test", "emailTest@naver.com", "hashedOldPassword");
        Hobby hobby = mock();
        TechStack techStack = mock();

        List<Long> hobbyIdList = List.of(hobbyId);
        List<Long> techStackIdList = List.of(techStackId);

        UserUpdateRequest request = Mockito.mock();

        when(hobby.getHobby()).thenReturn("hobby");
        when(techStack.getTechStack()).thenReturn("techStack");

        when(request.getName()).thenReturn("Test");
        when(request.getHobbyIdList()).thenReturn(hobbyIdList);
        when(request.getTechStackIdList()).thenReturn(techStackIdList);

        when(userRepository.findByIdWithInfo(userId)).thenReturn(Optional.of(user));
        when(hobbyRepositoryQuery.findByIdList(hobbyIdList)).thenReturn(List.of(hobby));
        when(techStackRepositoryQuery.findByIdList(techStackIdList)).thenReturn(List.of(techStack));

        // when
        UserResponseDto result = userService.updateMyProfile(userId, request);

        // then
        verify(userRepository).save(any(User.class)); // 저장 메서드 호출 확인
        assertEquals("Test", result.getName());
        assertEquals(List.of("hobby"), result.getHobbyList());
        assertEquals(List.of("techStack"), result.getTechStackList());
    }

    @Test
    void 비밀번호_수정_성공() {
        Long userId = 1L;
        String oldPassword = "1234";
        String newPassword = "5678";

        // 실제 암호화된 비밀번호
        String hashedOldPassword = BCrypt.withDefaults().hashToString(10, oldPassword.toCharArray());
        // User user = new User(userId, "Test", hashedOldPassword);
        User user = User.createTestUser(userId, "Test", "emailTest@naver.com", hashedOldPassword);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserPasswordUpdateRequest request = mock();
        when(request.getNewPassword()).thenReturn(newPassword);
        when(request.getOldPassword()).thenReturn(oldPassword);

        userService.updatePassword(userId, request);

        verify(userRepository).save(user);
        assertNotEquals(hashedOldPassword, user.getPassword()); // 비밀번호 바뀌었는지 확인
    }

    @Test
    void 비밀번호_수정_실패_비밀번호_불일치() {
        // given
        Long userId = 1L;
        String oldPassword = "1234";
        String wrongPassword = "9999";
        String newPassword = "5678";

        String hashedOldPassword = BCrypt.withDefaults().hashToString(10, oldPassword.toCharArray());
        User user = mock();
        when(user.getPassword()).thenReturn(hashedOldPassword);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        UserPasswordUpdateRequest request = mock();
        when(request.getOldPassword()).thenReturn(wrongPassword);

        assertThrows(BaseException.class, () -> userService.updatePassword(userId, request));
    }

    @Test
    void 비밀번호_수정_실패_유저_없음() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserPasswordUpdateRequest request = mock();

        assertThrows(BaseException.class, () -> userService.updatePassword(userId, request));
    }

    @Test
    void 사용자_평가_조회_성공() {
        Long userId = 1L;
        // User user = new User(userId, "Test", 45L, 9L);
        User user = mock();
        when(user.getId()).thenReturn(userId);
        when(user.getTotalReviewer()).thenReturn(9L);
        when(user.getTotalRate()).thenReturn(45L);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserEvaluationResponse result = userService.getEvaluation(userId);

        assertEquals(userId, result.getId());
        assertEquals(5.0f, result.getRate());
    }

    @Test
    void 사용자_평가_조회_실패_유저_없음() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(BaseException.class, () -> userService.getEvaluation(userId));
    }
}
