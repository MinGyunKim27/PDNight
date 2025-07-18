package org.example.pdnight.domain.techStack.dto.response;


import lombok.AllArgsConstructor;
import org.example.pdnight.domain.techStack.entity.TechStack;

@AllArgsConstructor
public class TechStackResponseDto {
    private Long id;
    private String techStack;

    public static TechStackResponseDto of(TechStack techStack){
        return new TechStackResponseDto(techStack.getId(),techStack.getTechStack());
    }
}
