package org.example.pdnight.domain.user.application.techStackUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.user.domain.entity.TechStack;
import org.example.pdnight.domain.user.domain.teckStackDomain.TechStackCommander;
import org.example.pdnight.domain.user.domain.teckStackDomain.TechStackReader;
import org.example.pdnight.domain.user.presentation.dto.techStackDto.request.TechStackRequest;
import org.example.pdnight.domain.user.presentation.dto.techStackDto.response.TechStackResponse;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TechStackCommanderService {

    private final TechStackCommander techStackCommander;
    private final TechStackReader techStackReader;

    public TechStackResponse createTechStack(TechStackRequest dto) {

        // DB에 존재 하는지 확인
        ifTechExists(dto);

        TechStack techStack = TechStack.create(dto.getTechStack());
        TechStack save = techStackCommander.save(techStack);
        return TechStackResponse.from(save);
    }

    // validate
    private void ifTechExists(TechStackRequest dto){
        boolean exists = techStackReader.existsTechStackByTechStack(dto.getTechStack());
        if (exists) {
            throw new BaseException(ErrorCode.TECH_STACK_ALREADY_EXISTS);
        }
    }
}
