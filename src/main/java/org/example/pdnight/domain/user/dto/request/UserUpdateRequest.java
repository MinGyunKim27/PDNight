package org.example.pdnight.domain.user.dto.request;

import lombok.Getter;

@Getter
public class UserUpdateRequest {
    // todo : 포맷 설정 통일
    private String email;
    private String hobbies;
    private String techStacks;
    private String name;
    private String nickname;
    private String gender;
    private Long age;
    private String jobCategory;
    private String region;
    private String comment;
}