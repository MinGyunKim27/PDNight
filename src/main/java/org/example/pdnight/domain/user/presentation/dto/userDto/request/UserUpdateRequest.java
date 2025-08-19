package org.example.pdnight.domain.user.presentation.dto.userDto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class UserUpdateRequest {
    @Schema(description = "취미 ID 목록", example = "[1, 2]")
    private List<Long> hobbyIdList;

    @Schema(description = "기술 스택 ID 목록", example = "[2]")
    private List<Long> techStackIdList;

    @Schema(description = "사용자 이름", example = "name")
    private String name;

    @Schema(description = "사용자 닉네임", example = "nickname")
    private String nickname;

    @Schema(description = "사용자 성별", example = "MALE")
    private String gender;

    @Schema(description = "사용자 나이", example = "30")
    private Long age;

    @Schema(description = "사용자 직업 카테고리", example = "BACK_END_DEVELOPER")
    private String jobCategory;

    @Schema(description = "사용자 지역", example = "PANGYO_DONG")
    private String region;

    @Schema(description = "사용자 한마디", example = "안녕하세요! 5년차 백엔드 개발자입니다. Spring Boot와 마이크로서비스 아키텍처에 깊은 관심이 있으며, 클린코드와 TDD를 지향합니다.")
    private String comment;
}
