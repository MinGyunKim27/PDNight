package org.example.pdnight.domain.post.dto.request;

import java.time.LocalDateTime;

import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostRequestDto {

	@NotBlank(message = "제목은 필수 입력값입니다.")
	private String title;

	@NotNull(message = "시간은 필수 입력값입니다.")
	private LocalDateTime timeSlot;

	private String publicContent;
	private String privateContent;

	private PostStatus status;

	@Min(value = 1, message = "참가인원은 최소 1인이상입니다.")
	private Integer maxParticipants;
	private Gender genderLimit;
	private JobCategory jobCategoryLimit;
	private AgeLimit ageLimit;

}
