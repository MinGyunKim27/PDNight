package org.example.pdnight.domain.user.application.techStackUseCase;

import org.example.pdnight.domain.user.domain.entity.TechStack;
import org.example.pdnight.domain.user.domain.teckStackDomain.TechStackReader;
import org.example.pdnight.domain.user.presentation.dto.techStackDto.response.TechStackResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TechStackReaderServiceTest {

    @InjectMocks
    TechStackReaderService techStackReaderService;

    @Mock
    TechStackReader techStackReader;

    @Test
    void searchTechStackList_성공() {
        // given
        String keyword = "Spring";

        TechStack techStack1 = TechStack.create("Spring Boot");
        TechStack techStack2 = TechStack.create("Spring Security");

        List<TechStack> resultList = List.of(techStack1, techStack2);

        when(techStackReader.searchTechStack(keyword)).thenReturn(resultList);

        // when
        List<TechStackResponse> results = techStackReaderService.searchTechStackList(keyword);

        // then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getTechStack()).contains("Spring");

        verify(techStackReader).searchTechStack(keyword);
    }
}
