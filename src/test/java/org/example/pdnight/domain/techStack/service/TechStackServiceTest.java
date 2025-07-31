package org.example.pdnight.domain.techStack.service;

import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.user.application.techStackUseCase.TechStackService;
import org.example.pdnight.domain.user.domain.entity.TechStack;
import org.example.pdnight.domain.user.domain.teckStackDomain.TechStackCommandQuery;
import org.example.pdnight.domain.user.domain.teckStackDomain.TechStackReader;
import org.example.pdnight.domain.user.infra.techStackInfra.TechStackJpaRepository;
import org.example.pdnight.domain.user.infra.techStackInfra.TechStackQueryRepositoryImpl;
import org.example.pdnight.domain.user.presentation.dto.techStackDto.request.TechStackRequest;
import org.example.pdnight.domain.user.presentation.dto.techStackDto.response.TechStackResponse;
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
    private TechStackReader techStackReader;

    @Mock
    private TechStackCommandQuery techStackCommandQuery;

    @Test
    void 기술스택_생성_성공() {
        //given
        TechStackRequest dto = new TechStackRequest("Spring Boot");
        TechStack techStack = TechStack.create("Spring Boot");

        when(techStackReader.existsTechStackByTechStack(dto.getTechStack())).thenReturn(false);
        when(techStackCommandQuery.save(any(TechStack.class))).thenReturn(techStack);

        //when
        TechStackResponse response = techStackService.createTechStack(dto);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getTechStack()).isEqualTo("Spring Boot");

        verify(techStackReader).existsTechStackByTechStack("Spring Boot");
        verify(techStackCommandQuery).save(any(TechStack.class));
    }

    @Test
    void createTechStack_중복예외() {
        // given
        TechStackRequest dto = new TechStackRequest("Spring Boot");

        when(techStackReader.existsTechStackByTechStack(dto.getTechStack())).thenReturn(true);

        // when & then
        BaseException exception = assertThrows(BaseException.class, () -> {
            techStackService.createTechStack(dto);
        });

        assertThat(exception.getMessage()).isEqualTo("이미 존재하는 기술 스택입니다");

        verify(techStackReader).existsTechStackByTechStack("Spring Boot");
        verify(techStackCommandQuery, never()).save(any());
    }

    @Test
    void searchTechStackList_성공() {
        // given
        String keyword = "Spring";

        TechStack techStack1 = TechStack.create("Spring Boot");
        TechStack techStack2 = TechStack.create("Spring Security");

        List<TechStack> resultList = List.of(techStack1, techStack2);

        when(techStackReader.searchTechStack(keyword)).thenReturn(resultList);

        // when
        List<TechStackResponse> results = techStackService.searchTechStackList(keyword);

        // then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getTechStack()).contains("Spring");

        verify(techStackReader).searchTechStack(keyword);
    }

}
