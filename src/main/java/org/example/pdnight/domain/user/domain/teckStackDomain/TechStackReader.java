package org.example.pdnight.domain.user.domain.teckStackDomain;

import jakarta.validation.constraints.NotNull;
import org.example.pdnight.domain.user.domain.entity.TechStack;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TechStackReader {

    List<TechStack> searchTechStack(String techStack);

    Collection<TechStack> findByIdList(List<Long> ids);

    boolean existsTechStackByTechStack(@NotNull String techStack);

    TechStack findByTechStack(String techStack);

    List<String> getNamesByIds(List<Long> techIds);

    Map<Long, String> getNamesByIdsMap(Set<Long> techIds);
}
