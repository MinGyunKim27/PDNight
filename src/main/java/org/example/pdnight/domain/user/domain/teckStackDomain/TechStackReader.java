package org.example.pdnight.domain.user.domain.teckStackDomain;

import jakarta.validation.constraints.NotNull;
import org.example.pdnight.domain.user.domain.entity.TechStack;


import java.util.Collection;
import java.util.List;

public interface TechStackReader {

    List<TechStack> searchTechStack(String techStack);

    Collection<TechStack> findByIdList(List<Long> ids);

    boolean existsTechStackByTechStack(@NotNull String techStack);

    TechStack findByTechStack(String techStack);
}
