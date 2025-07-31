package org.example.pdnight.domain.user.domain.hobbyDomain;

import jakarta.validation.constraints.NotNull;
import org.example.pdnight.domain.user.domain.entity.Hobby;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface HobbyReader {

    List<Hobby> searchHobby(String searchHobby);

    List<Hobby> findByIdList(List<Long> ids);

    Boolean existsHobbiesByHobby(String hobbyName);

    Hobby findByhobby(String hobbyName);

    List<String> getNamesByIds(List<Long> hobbyIds);

    Map<Long, String> getNamesByIdsMap(Set<Long> hobbyIds);
}
