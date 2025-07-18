package org.example.pdnight.domain.techStack.repository;

import org.example.pdnight.domain.techStack.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TechStackRepository extends JpaRepository<TechStack, Long> {
    List<TechStack> findByTechStackContaining(String techStack);
}
