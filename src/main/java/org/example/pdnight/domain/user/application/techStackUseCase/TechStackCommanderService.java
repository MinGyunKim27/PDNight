package org.example.pdnight.domain.user.application.techStackUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.entity.TechStack;
import org.example.pdnight.domain.user.domain.teckStackDomain.TechStackCommander;
import org.example.pdnight.domain.user.presentation.dto.techStackDto.request.TechStackRequest;
import org.example.pdnight.domain.user.presentation.dto.techStackDto.response.TechStackResponse;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TechStackCommanderService {

    private final TechStackCommander techStackCommander;

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
        boolean exists = techStackCommander.existsTechStackByTechStack(dto.getTechStack());
        if (exists) {
            throw new BaseException(ErrorCode.TECH_STACK_ALREADY_EXISTS);
        }
    }
}
