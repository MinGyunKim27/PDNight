package org.example.pdnight.domain.post.dto.response;

import java.time.LocalDateTime;

import lombok.NoArgsConstructor;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(force = true)
public class PostResponseDto {

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
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	public PostResponseDto(Post post) {
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
		this.createdAt = post.getCreatedAt();
		this.updatedAt = post.getUpdatedAt();
	}

	@QueryProjection
	public PostResponseDto(
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
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public static PostResponseDto toDto(Post post){
		return new PostResponseDto(
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
				post.getCreatedAt(),
				post.getUpdatedAt());
	}

}