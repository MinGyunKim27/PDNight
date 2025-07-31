package org.example.pdnight.domain.user.application.techStackUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.presentation.dto.techStackDto.request.TechStackRequest;
import org.example.pdnight.domain.user.presentation.dto.techStackDto.response.TechStackResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TechStackServiceImpl implements TechStackService {

    private final TechStackCommanderService techStackCommanderService;
    private final TechStackReaderService techStackReaderService;

    @Override
    public TechStackResponse createTechStack(TechStackRequest dto) {
        return techStackCommanderService.createTechStack(dto);
    }

    @Override
    public List<TechStackResponse> searchTechStackList(String techStack) {
        return techStackReaderService.searchTechStackList(techStack);
    }
}
