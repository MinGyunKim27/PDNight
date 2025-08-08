package org.example.pdnight.domain.user.application.techStackUseCase;

import org.example.pdnight.domain.user.domain.entity.TechStack;
import org.example.pdnight.domain.user.domain.teckStackDomain.TechStackCommander;
import org.example.pdnight.domain.user.presentation.dto.techStackDto.request.TechStackRequest;
import org.example.pdnight.domain.user.presentation.dto.techStackDto.response.TechStackResponse;
import org.example.pdnight.global.common.exception.BaseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TechStackCommanderServiceTest {

    @InjectMocks
    TechStackCommanderService techStackCommanderService;

    @Mock TechStackCommander techStackCommander;

    @Test
    void 기술스택_생성_성공() {
        //given
        TechStackRequest dto = new TechStackRequest("Spring Boot");
        TechStack techStack = TechStack.create("Spring Boot");

        when(techStackCommander.existsTechStackByTechStack(dto.getTechStack())).thenReturn(false);
        when(techStackCommander.save(any(TechStack.class))).thenReturn(techStack);

        //when
        TechStackResponse response = techStackCommanderService.createTechStack(dto);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getTechStack()).isEqualTo("Spring Boot");

        verify(techStackCommander).save(any(TechStack.class));
    }

    @Test
    void createTechStack_중복예외() {
        // given
        TechStackRequest dto = new TechStackRequest("Spring Boot");

        when(techStackCommander.existsTechStackByTechStack(dto.getTechStack())).thenReturn(true);

        // when & then
        BaseException exception = assertThrows(BaseException.class, () -> {
            techStackCommanderService.createTechStack(dto);
        });

        assertThat(exception.getMessage()).isEqualTo("이미 존재하는 기술 스택입니다");

        verify(techStackCommander, never()).save(any());
    }
}
