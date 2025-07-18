package org.example.pdnight.domain.techStack.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.techStack.entity.TechStack;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TechStackResponseDto {
    private Long id;
    private String techStack;

    public static TechStackResponseDto of(TechStack techStack){
        return new TechStackResponseDto(techStack.getId(),techStack.getTechStack());
    }
}
