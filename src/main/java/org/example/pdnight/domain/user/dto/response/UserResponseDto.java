package org.example.pdnight.domain.user.dto.response;

import lombok.Getter;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.common.enums.UserRole;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.enums.Region;
import java.time.LocalDateTime;

@Getter
public class UserResponseDto {

    private Long id;
    private String email;
    private UserRole role;
    private String hobbies;
    private String techStacks;
    private String name;
    private String nickname;
    private Gender gender;
    private Long age;
    private JobCategory jobCategory;
    private Region region;
    private Region workLocation;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.hobbies = user.getHobby().getHobby();
        this.techStacks = user.getTechStack().getTechStack();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.gender = user.getGender();
        this.age = user.getAge();
        this.jobCategory = user.getJobCategory();
        this.region = Region.valueOf(user.getRegion().getKoreanName());
        this.workLocation = Region.valueOf(user.getWorkLocation().getKoreanName());
        this.comment = user.getComment();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
}
