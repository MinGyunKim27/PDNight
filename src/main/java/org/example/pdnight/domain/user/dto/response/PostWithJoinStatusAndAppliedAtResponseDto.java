package org.example.pdnight.domain.user.dto.response;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.participant.enums.JoinStatus;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;

@Getter
public class PostWithJoinStatusAndAppliedAtResponseDto {

    private final Long postId;
    private final Long userId;
    private final String title;
    private final LocalDateTime timeslot;
    private final String publicContent;
    private final String privateContent;
    private final PostStatus status;
    private final Integer maxParticipants;
    private final Gender genderLimit;
    private final JobCategory jobCategoryLimit;
    private final AgeLimit ageLimit;
    private final JoinStatus joinStatus;
    private final LocalDateTime appliedAt;

    @QueryProjection
    public PostWithJoinStatusAndAppliedAtResponseDto(Long postId,
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
                                     LocalDateTime appliedAt) {
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
        this.appliedAt =  appliedAt;
    }
}

