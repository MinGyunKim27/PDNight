package org.example.pdnight.domain.user.dto.request;

import lombok.Getter;

@Getter
public class UserUpdateRequest {
    private Long hobbyId;
    private Long techStackId;
    private String name;
    private String nickname;
    private String gender;
    private Long age;
    private String jobCategory;
    private String region;
    private String comment;

    public UserUpdateRequest(String name, Long hobbyId) {
        this.name = name;
        this.hobbyId = hobbyId;
    }
}