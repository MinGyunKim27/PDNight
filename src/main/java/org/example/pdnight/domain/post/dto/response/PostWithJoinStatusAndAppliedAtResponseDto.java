package org.example.pdnight.domain.post.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.common.enums.JoinStatus;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PostWithJoinStatusAndAppliedAtResponseDto {

    private Long postId;
    private Long userId;
    private String title;
    private LocalDateTime timeslot;
    private String publicContent;
    private String privateContent;
    private PostStatus status;
    private Integer maxParticipants;
    private Gender genderLimit;
    private JobCategory jobCategoryLimit;
    private AgeLimit ageLimit;
    private JoinStatus joinStatus;
    private LocalDateTime appliedAt;

    @QueryProjection
    public PostWithJoinStatusAndAppliedAtResponseDto(
            Long postId,
            Long userId,
            String title,
            LocalDateTime timeslot,
            String publicContent,
            String privateContent,
            PostStatus status,
            Integer maxParticipants,
            Gender genderLimit,
            JobCategory jobCategoryLimit,
            AgeLimit ageLimit,
            JoinStatus joinStatus,
            LocalDateTime appliedAt
    ) {
        this.postId = postId;
        this.userId = userId;
        this.title = title;
        this.timeslot = timeslot;
        this.publicContent = publicContent;
        this.privateContent = privateContent;
        this.status = status;
        this.maxParticipants = maxParticipants;
        this.genderLimit = genderLimit;
        this.jobCategoryLimit = jobCategoryLimit;
        this.ageLimit = ageLimit;
        this.joinStatus = joinStatus;
        this.appliedAt = appliedAt;
    }
}
