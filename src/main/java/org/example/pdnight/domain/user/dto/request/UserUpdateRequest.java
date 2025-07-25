package org.example.pdnight.domain.user.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class UserUpdateRequest {
    private List<Long> hobbyIdList;
    private List<Long> techStackIdList;
    private String name;
    private String nickname;
    private String gender;
    private Long age;
    private String jobCategory;
    private String region;
    private String comment;

    // 테스트 실행용
    public UserUpdateRequest(String name, List<Long> hobbyIdList) {
        this.name = name;
        this.hobbyIdList = hobbyIdList;
    }
}
