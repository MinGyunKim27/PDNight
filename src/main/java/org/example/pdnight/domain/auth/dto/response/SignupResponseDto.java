package org.example.pdnight.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.common.enums.UserRole;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.enums.Region;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class SignupResponseDto {
    private String email;

    private UserRole role;

    private String name;

    private String nickname;

    private List<String> hobbyList;

    private List<String> techStackList;

    private Gender gender;

    private Long age;

    private JobCategory jobCategory;

    private Region region;

    private Region workLocation;

    private String comment;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    public SignupResponseDto(User user) {
        this.email = user.getEmail();
        this.role = UserRole.USER;
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.hobbyList = user.getUserHobbies()
                .stream()
                .map(hobby -> hobby.getHobby().getHobby())
                .toList();
        this.techStackList = user.getUserTechs()
                .stream()
                .map(tech -> tech.getTechStack().getTechStack())
                .toList();
        this.gender = user.getGender();
        this.age = user.getAge();
        this.jobCategory = user.getJobCategory();
        this.region = user.getRegion();
        this.workLocation = user.getWorkLocation();
        this.comment = user.getComment();
        this.createdAt = user.getCreatedAt();
        this.updateAt = user.getUpdatedAt();
    }
}
