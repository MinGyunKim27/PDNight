package org.example.pdnight.domain.post.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.global.common.enums.JobCategory;


import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PostRequest {

    @NotBlank(message = "제목은 필수 입력값입니다.")
    private String title;

    @NotNull(message = "시간은 필수 입력값입니다.")
    private LocalDateTime timeSlot;

    private String publicContent;

    @Min(value = 1, message = "참가인원은 최소 1인이상입니다.")
    private Integer maxParticipants;

    private Gender genderLimit;
    private JobCategory jobCategoryLimit;
    private AgeLimit ageLimit;
    private boolean isFirstCome;

}