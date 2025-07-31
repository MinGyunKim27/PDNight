package org.example.pdnight.domain.user.application.techStackUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.entity.TechStack;
import org.example.pdnight.domain.user.domain.teckStackDomain.TechStackReader;
import org.example.pdnight.domain.user.presentation.dto.techStackDto.response.TechStackResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TechStackReaderService {

    private final TechStackReader techStackReader;

    public List<TechStackResponse> searchTechStackList(String techStack) {
        List<TechStack> byTechStackContaining = techStackReader.searchTechStack(techStack);
        return byTechStackContaining.stream().map(TechStackResponse::from).toList();
    }
}
