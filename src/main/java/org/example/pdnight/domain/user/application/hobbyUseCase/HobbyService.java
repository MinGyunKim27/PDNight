package org.example.pdnight.domain.user.application.hobbyUseCase;

import org.example.pdnight.domain.user.presentation.dto.hobbyDto.request.HobbyRequest;
import org.example.pdnight.domain.user.presentation.dto.hobbyDto.response.HobbyResponse;

import java.util.List;

public interface HobbyService {

    HobbyResponse createHobby(HobbyRequest dto);

    List<HobbyResponse> searchHobby(String searchHobby);
}
