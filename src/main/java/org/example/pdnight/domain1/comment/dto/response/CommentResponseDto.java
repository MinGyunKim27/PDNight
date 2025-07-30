package org.example.pdnight.domain1.comment.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.pdnight.domain1.comment.entity.Comment;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentResponseDto {

	private final Long id;
	private final Long postId;
	private final Long authorId;
	private final String content;

	//대댓글일 경우에만 기입
	private final Long parentId;

	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	//리스트가 비어있으면 반환 안됨
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private final List<CommentResponseDto> children = new ArrayList<>();

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

	//댓글응답에 대댓글 추가 메서드
	public void addChild(CommentResponseDto commentResponseDto) {
		children.add(commentResponseDto);
	}

}