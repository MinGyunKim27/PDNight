package org.example.pdnight.domain.user.dto.request;

import lombok.Getter;

@Getter
public class UserUpdateRequest {
    private String email;
    private String role;
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