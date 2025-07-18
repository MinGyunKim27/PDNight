package org.example.pdnight.domain.post.dto.response;

import java.time.LocalDateTime;

import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponseDto {

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

	public PostResponseDto(Post post) {
		this.postId = post.getId();
		this.userId = post.getAuthor().getId();
		this.title = post.getTitle();
		this.timeslot = post.getTimeSlot();
		this.publicContent = post.getPublicContent();
		this.privateContent = post.getPrivateContent();
		this.status = post.getStatus();
		this.maxParticipants = post.getMaxParticipants();
		this.genderLimit = post.getGenderLimit();
		this.jobCategoryLimit = post.getJobCategoryLimit();
		this.ageLimit = post.getAgeLimit();
	}

}