package org.example.pdnight.domain1.post.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain1.common.enums.JobCategory;
import org.example.pdnight.domain1.hobby.entity.PostHobby;
import org.example.pdnight.domain1.post.entity.Post;
import org.example.pdnight.domain1.post.enums.AgeLimit;
import org.example.pdnight.domain1.post.enums.Gender;
import org.example.pdnight.domain1.post.enums.PostStatus;
import org.example.pdnight.domain1.techStack.entity.PostTech;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(force = true)
public class PostCreateAndUpdateResponseDto {

    private final Long postId;
    private final Long authorId;
    private final String title;
    private final LocalDateTime timeSlot;
    private final String publicContent;
    private final String privateContent;
    private final PostStatus status;
    private final Integer maxParticipants;
    private final Gender genderLimit;
    private final JobCategory jobCategoryLimit;
    private final AgeLimit ageLimit;
    private final List<String> hobbyList;
    private final List<String> techStackList;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public PostCreateAndUpdateResponseDto(Post post) {
        this.postId = post.getId();
        this.authorId = post.getAuthor().getId();
        this.title = post.getTitle();
        this.timeSlot = post.getTimeSlot();
        this.publicContent = post.getPublicContent();
        this.privateContent = post.getPrivateContent();
        this.status = post.getStatus();
        this.maxParticipants = post.getMaxParticipants();
        this.genderLimit = post.getGenderLimit();
        this.jobCategoryLimit = post.getJobCategoryLimit();
        this.ageLimit = post.getAgeLimit();
        this.hobbyList = post.getPostHobbies()
                .stream()
                .map(hobby -> hobby.getHobby().getHobby())
                .toList();
        this.techStackList = post.getPostTechs()
                .stream()
                .map(tech -> tech.getTechStack().getTechStack())
                .toList();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }

    public PostCreateAndUpdateResponseDto(
            Long id,
            Long authorId,
            String title,
            LocalDateTime timeSlot,
            String publicContent,
            String privateContent,
            PostStatus status,
            Integer maxParticipants,
            Gender genderLimit,
            JobCategory jobCategoryLimit,
            AgeLimit ageLimit,
            Set<PostHobby> hobbyList,
            Set<PostTech> techStackList,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.postId = id;
        this.authorId = authorId;
        this.title = title;
        this.timeSlot = timeSlot;
        this.publicContent = publicContent;
        this.privateContent = privateContent;
        this.status = status;
        this.maxParticipants = maxParticipants;
        this.genderLimit = genderLimit;
        this.jobCategoryLimit = jobCategoryLimit;
        this.ageLimit = ageLimit;
        this.hobbyList = hobbyList.stream()
                .map(hobby -> hobby.getHobby().getHobby())
                .toList();
        this.techStackList = techStackList.stream()
                .map(tech -> tech.getTechStack().getTechStack())
                .toList();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PostCreateAndUpdateResponseDto from(Post post) {
        return new PostCreateAndUpdateResponseDto(
                post.getId(),
                post.getAuthor().getId(),
                post.getTitle(),
                post.getTimeSlot(),
                post.getPublicContent(),
                post.getPrivateContent(),
                post.getStatus(),
                post.getMaxParticipants(),
                post.getGenderLimit(),
                post.getJobCategoryLimit(),
                post.getAgeLimit(),
                post.getPostHobbies(),
                post.getPostTechs(),
                post.getCreatedAt(),
                post.getUpdatedAt());
    }

}
