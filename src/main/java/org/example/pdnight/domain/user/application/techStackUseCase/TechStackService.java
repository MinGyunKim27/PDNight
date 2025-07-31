package org.example.pdnight.domain.user.application.techStackUseCase;


import org.example.pdnight.domain.user.presentation.dto.techStackDto.request.TechStackRequest;
import org.example.pdnight.domain.user.presentation.dto.techStackDto.response.TechStackResponse;

import java.util.List;

public interface TechStackService {

    TechStackResponse createTechStack(TechStackRequest dto);

    List<TechStackResponse> searchTechStackList(String techStack);

}
