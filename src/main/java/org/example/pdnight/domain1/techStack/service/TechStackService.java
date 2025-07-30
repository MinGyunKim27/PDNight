package org.example.pdnight.domain1.techStack.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain1.common.enums.ErrorCode;
import org.example.pdnight.domain1.common.exception.BaseException;
import org.example.pdnight.domain1.techStack.dto.request.TechStackRequestDto;
import org.example.pdnight.domain1.techStack.dto.response.TechStackResponseDto;
import org.example.pdnight.domain1.techStack.entity.TechStack;
import org.example.pdnight.domain1.techStack.repository.TechStackRepository;
import org.example.pdnight.domain1.techStack.repository.TechStackRepositoryQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TechStackService {

    private final TechStackRepository techStackRepository;
    private final TechStackRepositoryQuery techStackRepositoryQuery;

    public TechStackResponseDto createTechStack(TechStackRequestDto dto) {

        // DB에 존재 하는지 확인
        ifTechExists(dto);

        TechStack techStack = TechStack.create(dto.getTechStack());
        TechStack save = techStackRepository.save(techStack);
        return TechStackResponseDto.from(save);
    }

    public List<TechStackResponseDto> searchTechStackList(String techStack) {
        List<TechStack> byTechStackContaining = techStackRepositoryQuery.searchTechStack(techStack);
        return byTechStackContaining.stream().map(TechStackResponseDto::from).toList();
    }

    // -- HELPER 메서드 -- //

    // validate
    private void ifTechExists(TechStackRequestDto dto){
        boolean exists = techStackRepository.existsTechStackByTechStack(dto.getTechStack());
        if (exists) {
            throw new BaseException(ErrorCode.TECH_STACK_ALREADY_EXISTS);
        }
    }
}
