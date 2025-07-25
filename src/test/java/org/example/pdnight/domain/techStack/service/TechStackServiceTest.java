package org.example.pdnight.domain.techStack.service;

import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.techStack.dto.request.TechStackRequestDto;
import org.example.pdnight.domain.techStack.dto.response.TechStackResponseDto;
import org.example.pdnight.domain.techStack.entity.TechStack;
import org.example.pdnight.domain.techStack.repository.TechStackRepository;
import org.example.pdnight.domain.techStack.repository.TechStackRepositoryQueryImpl;
import org.example.pdnight.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TechStackServiceTest {
    @InjectMocks
    private TechStackService techStackService;

    @Mock
    private TechStackRepository techStackRepository;

    @Mock
    private TechStackRepositoryQueryImpl techStackRepositoryQuery;

    @Test
    void 기술스택_생성_성공(){
        //given
        TechStackRequestDto dto = new TechStackRequestDto("Spring Boot");
        TechStack techStack = new TechStack("Spring Boot");

        when(techStackRepository.existsTechStackByTechStack(dto.getTechStack())).thenReturn(false);
        when(techStackRepository.save(any(TechStack.class))).thenReturn(techStack);

        //when
        TechStackResponseDto response = techStackService.createTechStack(dto);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getTechStack()).isEqualTo("Spring Boot");

        verify(techStackRepository).existsTechStackByTechStack("Spring Boot");
        verify(techStackRepository).save(any(TechStack.class));
    }
    @Test
    void createTechStack_중복예외() {
        // given
        TechStackRequestDto dto = new TechStackRequestDto("Spring Boot");

        when(techStackRepository.existsTechStackByTechStack(dto.getTechStack())).thenReturn(true);

        // when & then
        BaseException exception = assertThrows(BaseException.class, () -> {
            techStackService.createTechStack(dto);
        });

        assertThat(exception.getMessage()).isEqualTo("이미 존재하는 기술 스택입니다");

        verify(techStackRepository).existsTechStackByTechStack("Spring Boot");
        verify(techStackRepository, never()).save(any());
    }

    @Test
    void searchTechStackList_성공() {
        // given
        String keyword = "Spring";
        List<TechStack> resultList = List.of(
                new TechStack("Spring Boot"),
                new TechStack("Spring Security")
        );

        when(techStackRepositoryQuery.searchTechStack(keyword)).thenReturn(resultList);

        // when
        List<TechStackResponseDto> results = techStackService.searchTechStackList(keyword);

        // then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getTechStack()).contains("Spring");

        verify(techStackRepositoryQuery).searchTechStack(keyword);
    }

}
