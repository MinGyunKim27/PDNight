package org.example.pdnight.domain.comment.dto.response;

import java.time.LocalDateTime;

import org.example.pdnight.domain.comment.entity.Comment;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentResponseDto {

	private final Long id;
	private final Long postId;
	private final Long authorId;
	private final String content;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	//대댓글일 경우에만 기입
	private final Long parentId;

	private CommentResponseDto(Comment comment) {
		this.id = comment.getId();
		this.postId = comment.getPost().getId();
		this.authorId = comment.getAuthor().getId();
		this.content = comment.getContent();
		this.createdAt = comment.getCreatedAt();
		this.updatedAt = comment.getUpdatedAt();
		this.parentId = comment.getParent() != null ? comment.getParent().getId() : null;
	}

	public static CommentResponseDto from(Comment comment) {
		return new CommentResponseDto(comment);
	}

}