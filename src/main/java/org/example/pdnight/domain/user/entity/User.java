package org.example.pdnight.domain.user.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.auth.dto.request.SignupRequestDto;
import org.example.pdnight.domain.common.entity.Timestamped;
import org.example.pdnight.domain.common.enums.UserRole;
import org.example.pdnight.domain.hobby.entity.Hobby;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.techStack.entity.TechStack;
import org.example.pdnight.domain.user.dto.request.UserUpdateRequest;
import org.example.pdnight.domain.user.enums.Region;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToOne
    @JoinColumn(name = "hobby_id")
    private Hobby hobby;

    @ManyToOne
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
    private Region workLocation = Region.PANGYO_DONG;

    private String comment;

    private Long totalRate;
    private Long totalReviewer;

    private Boolean isDeleted = false;
    private LocalDateTime deletedAt;

    public User(SignupRequestDto request, String encodePassword, Hobby hobby, TechStack techStack) {
        this.email = request.getEmail();
        this.password = encodePassword;
        this.role = UserRole.USER;
        this.name = request.getName();
        this.nickname = request.getNickname();
        this.hobby = hobby;
        this.techStack = techStack;
        this.gender = request.getGender();
        this.age =  request.getAge();
        this.jobCategory = request.getJobCategory();
        this.region = request.getRegion();
        this.workLocation = request.getWorkLocation();
        this.comment = request.getComment();
        this.totalRate = 0L;
        this.totalReviewer = 0L;
    }

    public void softDelete() {
        isDeleted = true;
        deletedAt = LocalDateTime.now();
    }
  
    public void updateProfile(UserUpdateRequest request) {
        if (request.getEmail() != null) {
            this.email = request.getEmail();
        }

        if (request.getName() != null) {
            this.name = request.getName();
        }
        if (request.getNickname() != null) {
            this.nickname = request.getNickname();
        }
        if (request.getGender() != null) {
            this.gender = Gender.valueOf(request.getGender());
        }
        if (request.getAge() != null) {
            this.age = request.getAge();
        }
        if (request.getJobCategory() != null) {
            this.jobCategory = JobCategory.valueOf(request.getJobCategory());
        }
        if (request.getRegion() != null) {
            this.region = Region.valueOf(request.getRegion());
        }
        if (request.getComment() != null){
            this.comment = request.getComment();
        }
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
  
}
