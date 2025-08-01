package org.example.pdnight.domain.user.application.userUseCase;

import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.userDomain.UserReader;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserReaderServiceTest {
    @Mock
    private UserReader userReader;
    @Mock
    private UserInfoAssembler userInfoAssembler;
    @InjectMocks
    private UserReaderService userReaderService;

    @Test
    void 내_프로필_조회_성공() {
        // given
        Long userId = 1L;
        User user = mock(User.class); // 혹은 new User(...)로 진짜 객체 생성
        UserResponse response = mock(UserResponse.class);

        when(userReader.findById(userId)).thenReturn(Optional.of(user));
        when(userInfoAssembler.toDto(user)).thenReturn(response);
        when(response.getId()).thenReturn(userId);
        when(response.getName()).thenReturn("Test");

        // when
        UserResponse result = userReaderService.getProfile(userId);

        // then
        assertEquals(userId, result.getId());
        assertEquals("Test", result.getName());
    }
}
