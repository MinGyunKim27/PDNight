package org.example.pdnight.domain.hobby.service;

import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.user.presentation.dto.hobbyDto.request.HobbyRequest;
import org.example.pdnight.domain.user.presentation.dto.hobbyDto.response.HobbyResponse;
import org.example.pdnight.domain.hobby.domain.entity.Hobby;
import org.example.pdnight.domain.hobby.infra.HobbyJpaRepository;
import org.example.pdnight.domain.hobby.infra.HobbyRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class HobbyServiceTest {

    @InjectMocks
    private HobbyService hobbyService;

    @Mock
    private HobbyJpaRepository hobbyRepository;

    @Mock
    private HobbyRepositoryImpl hobbyRepositoryQuery;

    @Test
    void createHobby_성공() {
        // given
        HobbyRequest dto = mock();
        Hobby hobby = mock();

        when(dto.getHobby()).thenReturn("등산");
        when(hobby.getHobby()).thenReturn("등산");

        when(hobbyRepository.existsHobbiesByHobby(dto.getHobby())).thenReturn(false);
        when(hobbyRepository.save(any(Hobby.class))).thenReturn(hobby);

        // when
        HobbyResponse response = hobbyService.createHobby(dto);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getHobby()).isEqualTo("등산");

        verify(hobbyRepository).existsHobbiesByHobby("등산");
        verify(hobbyRepository).save(any(Hobby.class));
    }

    @Test
    void createHobby_중복예외() {
        // given
        HobbyRequest dto = mock();

        when(dto.getHobby()).thenReturn("등산");
        when(hobbyRepository.existsHobbiesByHobby(dto.getHobby())).thenReturn(true);

        // when & then
        BaseException exception = assertThrows(BaseException.class, () -> {
            hobbyService.createHobby(dto);
        });

        assertThat(exception.getMessage()).isEqualTo("이미 존재하는 취미 입니다");

        verify(hobbyRepository).existsHobbiesByHobby("등산");
        verify(hobbyRepository, never()).save(any());
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

        when(hobbyRepositoryQuery.searchHobby(keyword)).thenReturn(hobbies);

        // when
        List<HobbyResponse> result = hobbyService.searchHobby(keyword);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getHobby()).contains("산");

        verify(hobbyRepositoryQuery).searchHobby(keyword);
    }
}
