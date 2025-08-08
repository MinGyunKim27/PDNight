package org.example.pdnight.domain.user.infra.techStackInfra;

import org.example.pdnight.domain.user.domain.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechStackJpaRepository extends JpaRepository<TechStack, Long> {

    boolean existsByTechStack(String techStack);
}
