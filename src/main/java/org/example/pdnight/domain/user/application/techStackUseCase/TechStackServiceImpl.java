package org.example.pdnight.domain.user.application.techStackUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.user.domain.teckStackDomain.TechStackCommandQuery;
import org.example.pdnight.domain.user.domain.teckStackDomain.TechStackReader;
import org.springframework.stereotype.Service;
import org.example.pdnight.domain.user.domain.entity.TechStack;
import org.example.pdnight.domain.user.presentation.dto.techStackDto.request.TechStackRequest;
import org.example.pdnight.domain.user.presentation.dto.techStackDto.response.TechStackResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TechStackServiceImpl {

    private final TechStackCommandQuery techStackCommandQuery;
    private final TechStackReader techStackReader;

    public TechStackResponse createTechStack(TechStackRequest dto) {

        // DB에 존재 하는지 확인
        ifTechExists(dto);

        TechStack techStack = TechStack.create(dto.getTechStack());
        TechStack save = techStackCommandQuery.save(techStack);
        return TechStackResponse.from(save);
    }

    public List<TechStackResponse> searchTechStackList(String techStack) {
        List<TechStack> byTechStackContaining = techStackReader.searchTechStack(techStack);
        return byTechStackContaining.stream().map(TechStackResponse::from).toList();
    }

    // -- HELPER 메서드 -- //

    // validate
    private void ifTechExists(TechStackRequest dto){
        boolean exists = techStackReader.existsTechStackByTechStack(dto.getTechStack());
        if (exists) {
            throw new BaseException(ErrorCode.TECH_STACK_ALREADY_EXISTS);
        }
    }

}
