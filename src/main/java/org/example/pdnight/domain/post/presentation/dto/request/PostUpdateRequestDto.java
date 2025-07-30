package org.example.pdnight.domain.post.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.JobCategory;
import org.example.pdnight.domain.post.enums.PostStatus;


import java.time.LocalDateTime;
import java.util.List;

//업데이트에 사용될 DTO, Null 아닌 필드만 업데이트
@Getter
@Builder
@AllArgsConstructor
public class PostUpdateRequestDto {

    private String title;
    private LocalDateTime timeSlot;
    private String publicContent;
    private String privateContent;
    private PostStatus status;

    //maxParticipants 는 1인 이상 검증해야함
    private Integer maxParticipants;
    private Gender genderLimit;
    private JobCategory jobCategoryLimit;
    private AgeLimit ageLimit;

    private List<Long> hobbyIdList;
    private List<Long> techStackIdList;

}
