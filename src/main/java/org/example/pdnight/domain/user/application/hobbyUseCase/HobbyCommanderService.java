package org.example.pdnight.domain.user.application.hobbyUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.entity.Hobby;
import org.example.pdnight.domain.user.domain.hobbyDomain.HobbyCommander;
import org.example.pdnight.domain.user.domain.hobbyDomain.HobbyReader;
import org.example.pdnight.domain.user.presentation.dto.hobbyDto.request.HobbyRequest;
import org.example.pdnight.domain.user.presentation.dto.hobbyDto.response.HobbyResponse;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HobbyCommanderService {

    private final HobbyCommander hobbyCommander;

    @Transactional
    public HobbyResponse createHobby(HobbyRequest dto){
        validateExistHobby(dto.getHobby());

        Hobby hobby = Hobby.from(dto.getHobby());

        Hobby save = hobbyCommander.save(hobby);
        return HobbyResponse.from(save);
    }

    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // validate
    private void validateExistHobby(String hobby) {
        if (!hobbyCommander.existsHobbiesByHobby(hobby)){
            throw new BaseException(ErrorCode.HOBBY_ALREADY_EXISTS);
        }
    }

}
