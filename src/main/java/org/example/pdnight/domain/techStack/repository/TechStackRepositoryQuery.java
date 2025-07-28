package org.example.pdnight.domain.techStack.repository;

import org.example.pdnight.domain.techStack.entity.TechStack;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TechStackRepositoryQuery {
    List<TechStack> searchTechStack(String techStack);

    List<TechStack> findByIdList(List<Long> ids);
}
