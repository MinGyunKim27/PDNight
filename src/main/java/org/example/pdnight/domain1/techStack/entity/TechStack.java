package org.example.pdnight.domain1.techStack.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain1.common.entity.Timestamped;

@Entity
@Table(name = "tech_stacks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TechStack extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tech_stack", unique = true)
    private String techStack;

    private TechStack(String techStack){
        this.techStack = techStack;
    }

    public static TechStack create(String techStack) {
        return new TechStack(techStack);
    }

}
