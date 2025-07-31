package org.example.pdnight.domain.user.application.techStackUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.presentation.dto.techStackDto.response.TechStackResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TechStackQueryService implements TechStackService{
    public List<TechStackResponse> searchTechStackList(String techStack){

    };
}
