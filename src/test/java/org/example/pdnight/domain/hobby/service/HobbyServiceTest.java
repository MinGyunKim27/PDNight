package org.example.pdnight.domain.hobby.service;

import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.user.application.hobbyUseCase.HobbyService;
import org.example.pdnight.domain.user.domain.entity.Hobby;
import org.example.pdnight.domain.user.domain.hobbyDomain.HobbyCommander;
import org.example.pdnight.domain.user.domain.hobbyDomain.HobbyReader;
import org.example.pdnight.domain.user.presentation.dto.hobbyDto.request.HobbyRequest;
import org.example.pdnight.domain.user.presentation.dto.hobbyDto.response.HobbyResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HobbyServiceTest {

    @InjectMocks
    private HobbyService hobbyService;

    @Mock
    private HobbyReader hobbyReader;

    @Mock
    private HobbyCommander hobbyCommandQuery;

    @Test
    void createHobby_성공() {
        // given
        HobbyRequest dto = mock();
        Hobby hobby = mock();

        when(dto.getHobby()).thenReturn("등산");
        when(hobby.getHobby()).thenReturn("등산");

        when(hobbyReader.existsHobbiesByHobby(dto.getHobby())).thenReturn(false);
        when(hobbyCommandQuery.save(any(Hobby.class))).thenReturn(hobby);

        // when
        HobbyResponse response = hobbyService.createHobby(dto);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getHobby()).isEqualTo("등산");

        verify(hobbyReader).existsHobbiesByHobby("등산");
        verify(hobbyCommandQuery).save(any(Hobby.class));
    }

    @Test
    void createHobby_중복예외() {
        // given
        HobbyRequest dto = mock();

        when(dto.getHobby()).thenReturn("등산");
        when(hobbyReader.existsHobbiesByHobby(dto.getHobby())).thenReturn(true);

        // when & then
        BaseException exception = assertThrows(BaseException.class, () -> {
            hobbyService.createHobby(dto);
        });

        assertThat(exception.getMessage()).isEqualTo("이미 존재하는 취미 입니다");

        verify(hobbyReader).existsHobbiesByHobby("등산");
        verify(hobbyCommandQuery, never()).save(any());
    }

    @Test
    void searchHobby_성공() {
        // given
        String keyword = "산";

        Hobby hobby1 = mock();
        Hobby hobby2 = mock();

        when(hobby1.getHobby()).thenReturn("등산");
        when(hobby2.getHobby()).thenReturn("산책");
        List<Hobby> hobbies = List.of(hobby1, hobby2);

        when(hobbyReader.searchHobby(keyword)).thenReturn(hobbies);

        // when
        List<HobbyResponse> result = hobbyService.searchHobby(keyword);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getHobby()).contains("산");

        verify(hobbyReader).searchHobby(keyword);
    }
}
