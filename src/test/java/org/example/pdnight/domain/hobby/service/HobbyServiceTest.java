package org.example.pdnight.domain.hobby.service;

import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.hobby.dto.request.HobbyRequest;
import org.example.pdnight.domain.hobby.dto.response.HobbyResponse;
import org.example.pdnight.domain.hobby.entity.Hobby;
import org.example.pdnight.domain.hobby.repository.HobbyRepository;
import org.example.pdnight.domain.hobby.repository.HobbyRepositoryQuery;
import org.example.pdnight.domain.user.entity.User;
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
    private HobbyRepository hobbyRepository;

    @Mock
    private HobbyRepositoryQuery hobbyRepositoryQuery;

    @Test
    void createHobby_성공() {
        // given
        HobbyRequest dto = new HobbyRequest("등산");
        Hobby hobby = new Hobby("등산");

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
        HobbyRequest dto = new HobbyRequest("등산");

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
        List<Hobby> hobbies = List.of(new Hobby("등산"), new Hobby("산책"));

        when(hobbyRepositoryQuery.searchHobby(keyword)).thenReturn(hobbies);

        // when
        List<HobbyResponse> result = hobbyService.searchHobby(keyword);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getHobby()).contains("산");

        verify(hobbyRepositoryQuery).searchHobby(keyword);
    }
}

