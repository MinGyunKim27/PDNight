package org.example.pdnight.domain.user.presentation.dto.userDto.request;

import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public class UserUpdateRequest {
    private Set<Long> hobbyIdList;
    private Set<Long> techStackIdList;
    private String name;
    private String nickname;
    private String gender;
    private Long age;
    private String jobCategory;
    private String region;
    private String comment;
}
