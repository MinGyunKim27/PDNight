package org.example.pdnight.domain.post.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentRequestDto {

	@NotBlank(message = "변경내용은 필수값입니다.")
	private String content;

}