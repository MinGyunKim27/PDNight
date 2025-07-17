package org.example.pdnight.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.common.entity.Timestamped;
import org.example.pdnight.domain.common.enums.UserRole;
import org.example.pdnight.domain.hobby.entity.Hobby;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.techStack.entity.TechStack;
import org.example.pdnight.domain.user.enums.Region;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User extends Timestamped {
    @Id
    private Long id;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false)
    private String name;

    private String nickname;

    @OneToOne
    @JoinColumn(name = "hobby_id")
    private Hobby hobby;

    @OneToOne
    @JoinColumn(name = "teck_stack_id")
    private TechStack techStack;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Long age;

    @Enumerated(EnumType.STRING)
    private JobCategory jobCategory;

    @Enumerated(EnumType.STRING)
    private Region region;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_location")
    private Region workLocation;

    private String comment;

    private Long totalRate;
    private Long totalReviewer;
}
