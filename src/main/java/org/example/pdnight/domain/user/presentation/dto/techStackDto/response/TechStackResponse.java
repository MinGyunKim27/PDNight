package org.example.pdnight.domain.user.presentation.dto.techStackDto.response;

import org.example.pdnight.domain.user.domain.entity.TechStack;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TechStackResponse {
    private Long id;
    private String techStack;

    public static TechStackResponse from(TechStack techStack){
        return new TechStackResponse(techStack.getId(),techStack.getTechStack());
    }
}
