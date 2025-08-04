package org.example.pdnight.domain.user.application.hobbyUseCase;

import org.example.pdnight.domain.user.domain.entity.Hobby;
import org.example.pdnight.domain.user.domain.hobbyDomain.HobbyReader;
import org.example.pdnight.domain.user.presentation.dto.hobbyDto.response.HobbyResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HobbyReaderServiceTest {
    @InjectMocks
    private HobbyReaderService hobbyService;

    @Mock
    private HobbyReader hobbyReader;

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
