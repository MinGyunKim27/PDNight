package org.example.pdnight.domain.user.dto.response;

import lombok.Getter;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.common.enums.UserRole;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.enums.Region;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class UserResponseDto {

    private Long id;
    private String email;
    private UserRole role;
    private List<String> hobbyList;
    private List<String> techStackList;
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
        this.hobbyList = user.getUserHobbies()
                .stream()
                .map(hobby -> hobby.getHobby().getHobby())
                .toList();
        this.techStackList = user.getUserTechs()
                .stream()
                .map(tech -> tech.getTechStack().getTechStack())
                .toList();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.gender = user.getGender();
        this.age = user.getAge();
        this.jobCategory = user.getJobCategory();
        this.region = user.getRegion();
        this.workLocation = user.getWorkLocation();
        this.comment = user.getComment();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
}
