package org.example.pdnight.domain.hobby.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.hobby.entity.Hobby;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HobbyResponse {
    private Long id;
    private String hobby;

    public static HobbyResponse of(Hobby hobby){
        return new HobbyResponse(hobby.getId(),hobby.getHobby());
    }
}
