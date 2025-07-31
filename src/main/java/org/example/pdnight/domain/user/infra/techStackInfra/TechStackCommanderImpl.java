package org.example.pdnight.domain.user.infra.techStackInfra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.entity.TechStack;
import org.example.pdnight.domain.user.domain.teckStackDomain.TechStackCommander;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TechStackCommanderImpl implements TechStackCommander {
    private final TechStackJpaRepository techStackJpaRepository;

    public TechStack save(TechStack techStack){
        return techStackJpaRepository.save(techStack);
    }
}
