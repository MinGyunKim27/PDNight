package org.example.pdnight.domain.techStack.repository;

import jakarta.validation.constraints.NotNull;
import org.example.pdnight.domain.techStack.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechStackRepository extends JpaRepository<TechStack, Long> {
    boolean existsTechStackByTechStack(@NotNull String techStack);
}
