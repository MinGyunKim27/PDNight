package org.example.pdnight.domain.techStack.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.techStack.dto.request.TechStackRequestDto;
import org.example.pdnight.domain.techStack.dto.response.TechStackResponseDto;
import org.example.pdnight.domain.techStack.entity.TechStack;
import org.example.pdnight.domain.techStack.repository.TechStackRepository;
import org.example.pdnight.domain.techStack.repository.TechStackRepositoryQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TechStackService {
    private final TechStackRepository techStackRepository;
    private final TechStackRepositoryQuery techStackRepositoryQuery;

    public TechStackResponseDto createTechStack(TechStackRequestDto dto) {
        TechStack techStack = new TechStack(dto.getTechStack());
        TechStack save = techStackRepository.save(techStack);
        return TechStackResponseDto.of(save);
    }

    public List<TechStackResponseDto> searchTechStackList(String techStack) {
        List<TechStack> byTechStackContaining = techStackRepositoryQuery.searchTechStack(techStack);
        return byTechStackContaining.stream().map(TechStackResponseDto::of).toList();
    }
}
