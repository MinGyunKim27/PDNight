package org.example.pdnight.domain.techStack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.common.entity.Timestamped;

@Entity
@Table(name = "tech_stacks")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TechStack extends Timestamped {
    @Id
    private Long id;

    @Column(name = "tech_stack", length = 255)
    private String techStack;

    public TechStack(String techStack){
        this.techStack = techStack;
    }
}
