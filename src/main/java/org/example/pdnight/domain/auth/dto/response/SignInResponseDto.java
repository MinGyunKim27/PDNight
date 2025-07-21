package org.example.pdnight.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.common.enums.UserRole;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.enums.Region;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SignInResponseDto {
    private String email;

    private UserRole role;

    private String name;

    private String nickname;

    private String hobby;

    private String techStack;

    private Gender gender;

    private Long age;

    private JobCategory jobCategory;

    private Region region;

    private Region workLocation;

    private String comment;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    public SignInResponseDto(User user, String hobby, String techStack) {
        this.email = user.getEmail();
        this.role = UserRole.USER;
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.hobby = hobby;
        this.techStack = techStack;
        this.gender = user.getGender();
        this.age =  user.getAge();
        this.jobCategory = user.getJobCategory();
        this.region = user.getRegion();
        this.workLocation = user.getWorkLocation();
        this.comment = user.getComment();
        this.createdAt = user.getCreatedAt();
        this.updateAt = user.getUpdatedAt();
    }
}
