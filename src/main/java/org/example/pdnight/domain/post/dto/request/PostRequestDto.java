package org.example.pdnight.domain.post.dto.request;

import java.time.LocalDateTime;

import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostRequestDto {

	private String title;
	private LocalDateTime timeslot;
	private String publicContent;
	private String privateContent;
	private PostStatus status;
	private Integer maxParticipants;
	private Gender genderLimit;
	private JobCategory jobCategoryLimit;
	private AgeLimit ageLimit;

}
