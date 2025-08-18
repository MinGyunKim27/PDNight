package org.example.pdnight.domain.user.application.userUseCase;

import java.util.List;

public interface TechStackPort {
    List<String> getTechStackNames(List<Long> techStackIds);
}
