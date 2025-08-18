package org.example.pdnight.domain.user.presentation.dto.userDto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.enums.Region;
import org.example.pdnight.global.common.enums.JobCategory;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserResponse {

    private Long id;
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

    private UserResponse(User user, List<String> hobbyNames, List<String> techNames) {
        this.id = user.getId();
        this.hobbyList = hobbyNames;
        this.techStackList = techNames;
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

    @QueryProjection
    public UserResponse(
            Long id,
            String name,
            String nickname,
            Gender gender,
            Long age,
            JobCategory jobCategory,
            Region region,
            Region workLocation,
            String comment,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.gender = gender;
        this.age = age;
        this.jobCategory = jobCategory;
        this.region = region;
        this.workLocation = workLocation;
        this.comment = comment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserResponse from(User user, List<String> hobbyNames, List<String> techNames) {
        return new UserResponse(user, hobbyNames, techNames);
    }

    public void setHobbyAndTech(List<String> hobbyList, List<String> techList) {
        this.hobbyList = hobbyList;
        this.techStackList = techList;
    }
}
