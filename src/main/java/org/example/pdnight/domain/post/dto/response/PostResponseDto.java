package org.example.pdnight.domain.post.dto.response;

import java.time.LocalDateTime;

import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
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

}