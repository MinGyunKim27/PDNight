package org.example.pdnight.domain.user.application.techStackUseCase;


import org.example.pdnight.domain.user.presentation.dto.techStackDto.request.TechStackRequestDto;
import org.example.pdnight.domain.user.presentation.dto.techStackDto.response.TechStackResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TechStackService {
    TechStackResponseDto createTechStack(TechStackRequestDto dto);

    List<TechStackResponseDto> searchTechStackList(String techStack);

}
