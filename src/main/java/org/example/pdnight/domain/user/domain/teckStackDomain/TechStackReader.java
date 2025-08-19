package org.example.pdnight.domain.user.domain.teckStackDomain;

import org.example.pdnight.domain.user.domain.entity.TechStack;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TechStackReader {

    List<TechStack> searchTechStack(String techStack);

    List<String> getNamesByIds(List<Long> techIds);

    Map<Long, String> getNamesByIdsMap(Set<Long> techIds);
}
