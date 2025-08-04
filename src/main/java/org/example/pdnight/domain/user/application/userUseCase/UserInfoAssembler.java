package org.example.pdnight.domain.user.application.userUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.entity.UserHobby;
import org.example.pdnight.domain.user.domain.entity.UserTech;
import org.example.pdnight.domain.user.domain.hobbyDomain.HobbyReader;
import org.example.pdnight.domain.user.domain.teckStackDomain.TechStackReader;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserInfoAssembler {

    private final HobbyReader hobbyReader;
    private final TechStackReader techStackReader;

    public UserResponse toDto(User user) {
        List<Long> hobbyIds = user.getUserHobbies().stream()
                .map(UserHobby::getHobbyId)
                .toList();

        List<Long> techIds = user.getUserTeckStacks().stream()
                .map(UserTech::getTechStackId)
                .toList();

        List<String> hobbyNames = hobbyReader.getNamesByIds(hobbyIds);
        List<String> techNames = techStackReader.getNamesByIds(techIds);

        return UserResponse.from(user, hobbyNames, techNames);
    }

    //toDto
    public List<UserResponse> toDtoList(Page<User> page){
        List<User> users = page.getContent();

        Set<Long> hobbyIds = users.stream()
                .flatMap(u -> u.getUserHobbies().stream())
                .map(UserHobby::getHobbyId)
                .collect(Collectors.toSet());

        Set<Long> techStackIds = users.stream()
                .flatMap(u -> u.getUserTeckStacks().stream())
                .map(UserTech::getTechStackId)
                .collect(Collectors.toSet());

        Map<Long, String> hobbyMap = hobbyReader.getNamesByIdsMap(hobbyIds);
        Map<Long, String> techMap = techStackReader.getNamesByIdsMap(techStackIds);

        return users.stream()
                .map(user -> {
                    List<String> hobbyNames = user.getUserHobbies().stream()
                            .map(uh -> hobbyMap.getOrDefault(uh.getHobbyId(), ""))
                            .toList();

                    List<String> techNames = user.getUserTeckStacks().stream()
                            .map(ut -> techMap.getOrDefault(ut.getTechStackId(), ""))
                            .toList();

                    return UserResponse.from(user, hobbyNames, techNames);
                })
                .toList();
    }
}

