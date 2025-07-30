package org.example.pdnight.domain1.hobby.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain1.hobby.entity.Hobby;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HobbyResponse {
    private Long id;
    private String hobby;

    private HobbyResponse(Hobby hobby) {
        this.id = hobby.getId();
        this.hobby = hobby.getHobby();
    }

    public static HobbyResponse from(Hobby hobby){
        return new HobbyResponse(hobby);
    }

}
