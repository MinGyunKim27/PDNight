package org.example.pdnight.domain.user.application.hobbyUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.user.domain.entity.Hobby;
import org.example.pdnight.domain.user.infra.hobbyInfra.HobbyJpaRepository;
import org.example.pdnight.domain.user.infra.hobbyInfra.HobbyRepositoryImpl;
import org.example.pdnight.domain.user.presentation.dto.hobbyDto.request.HobbyRequest;
import org.example.pdnight.domain.user.presentation.dto.hobbyDto.response.HobbyResponse;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class HobbyServiceImpl implements HobbyService {

    private final HobbyJpaRepository hobbyRepository;
    private final HobbyRepositoryImpl hobbyRepositoryQuery;

    public HobbyResponse createHobby(HobbyRequest dto){
        validateExistHobby(dto.getHobby());

        Hobby hobby = Hobby.from(dto.getHobby());

        Hobby save = hobbyRepository.save(hobby);
        return HobbyResponse.from(save);
    }

    public List<HobbyResponse> searchHobby(String searchHobby){
        List<Hobby> hobbies = hobbyRepositoryQuery.searchHobby(searchHobby);
        return hobbies.stream().map(HobbyResponse::from).toList();
    }

    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // validate
    private void validateExistHobby(String hobby) {
        Boolean exists = hobbyRepository.existsHobbiesByHobby(hobby);
        if (exists){
            throw new BaseException(ErrorCode.HOBBY_ALREADY_EXISTS);
        }
    }
}
