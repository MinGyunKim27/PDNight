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
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TechStackCommanderService {

    private final TechStackCommander techStackCommander;
    private final TechStackReader techStackReader;

    @Transactional
    public TechStackResponse createTechStack(TechStackRequest dto) {
        validateExistTech(dto);

        TechStack techStack = TechStack.create(dto.getTechStack());

        TechStack save = techStackCommander.save(techStack);
        return TechStackResponse.from(save);
    }

    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // validate
    private void validateExistTech(TechStackRequest dto){
        boolean exists = techStackReader.existsTechStackByTechStack(dto.getTechStack());
        if (exists) {
            throw new BaseException(ErrorCode.TECH_STACK_ALREADY_EXISTS);
        }
    }
}
