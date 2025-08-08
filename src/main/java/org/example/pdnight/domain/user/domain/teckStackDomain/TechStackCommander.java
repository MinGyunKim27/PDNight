package org.example.pdnight.domain.user.domain.teckStackDomain;

import org.example.pdnight.domain.user.domain.entity.TechStack;

public interface TechStackCommander {
    TechStack save(TechStack techStack);

    boolean existsTechStackByTechStack(String techStack);
}
