package org.example.pdnight.domain.user.infra.hobbyInfra;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HobbyRepositoryImpl {

    private final HobbyJpaRepository hobbyJpaRepository;


}
