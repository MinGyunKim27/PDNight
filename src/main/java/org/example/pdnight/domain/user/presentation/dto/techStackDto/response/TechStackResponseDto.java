package org.example.pdnight.domain.user.presentation.dto.techStackDto.response;

import org.example.pdnight.domain.user.domain.entity.TechStack;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TechStackResponseDto {
    private Long id;
    private String techStack;

    public static TechStackResponseDto from(TechStack techStack){
        return new TechStackResponseDto(techStack.getId(),techStack.getTechStack());
    }
}
