package org.example.pdnight.domain.user.infra.techStackInfra;

import jakarta.validation.constraints.NotNull;
import org.example.pdnight.domain.user.domain.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechStackJpaRepository extends JpaRepository<TechStack, Long> {

}
