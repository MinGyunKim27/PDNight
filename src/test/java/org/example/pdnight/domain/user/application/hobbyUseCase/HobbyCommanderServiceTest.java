package org.example.pdnight.domain.user.application.hobbyUseCase;

import org.example.pdnight.domain.user.domain.entity.Hobby;
import org.example.pdnight.domain.user.domain.hobbyDomain.HobbyCommander;
import org.example.pdnight.domain.user.presentation.dto.hobbyDto.request.HobbyRequest;
import org.example.pdnight.domain.user.presentation.dto.hobbyDto.response.HobbyResponse;
import org.example.pdnight.global.common.exception.BaseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HobbyCommanderServiceTest {
    @InjectMocks
    private HobbyCommanderService hobbyService;

    @Mock
    private HobbyCommander hobbyCommander;

    @Test
    void createHobby_성공() {
        // given
        HobbyRequest dto = mock();
        Hobby hobby = mock();

        when(dto.getHobby()).thenReturn("등산");
        when(hobby.getHobby()).thenReturn("등산");

        when(hobbyCommander.existsHobbiesByHobby(dto.getHobby())).thenReturn(false);
        when(hobbyCommander.save(any(Hobby.class))).thenReturn(hobby);

        // when
        HobbyResponse response = hobbyService.createHobby(dto);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getHobby()).isEqualTo("등산");

        verify(hobbyCommander).save(any(Hobby.class));
    }

    @Test
    void createHobby_중복예외() {
        // given
        HobbyRequest dto = mock();

        when(dto.getHobby()).thenReturn("등산");
        when(hobbyCommander.existsHobbiesByHobby(dto.getHobby())).thenReturn(true);

        // when & then
        BaseException exception = assertThrows(BaseException.class, () -> {
            hobbyService.createHobby(dto);
        });

        assertThat(exception.getMessage()).isEqualTo("이미 존재하는 취미 입니다");

        verify(hobbyCommander, never()).save(any());
    }
}
