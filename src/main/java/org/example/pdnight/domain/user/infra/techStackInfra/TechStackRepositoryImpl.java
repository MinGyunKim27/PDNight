package org.example.pdnight.domain.user.infra.techStackInfra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.entity.TechStack;
import org.example.pdnight.domain.user.domain.teckStackDomain.TechStackCommandQuery;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TechStackRepositoryImpl implements TechStackCommandQuery {
    private final TechStackJpaRepository techStackJpaRepository;

    public TechStack save(TechStack techStack){
        return techStackJpaRepository.save(techStack);
    }
    public void delete(TechStack techStack){
        techStackJpaRepository.delete(techStack);
    }
}
