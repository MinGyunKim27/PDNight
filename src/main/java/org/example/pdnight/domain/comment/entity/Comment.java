package org.example.pdnight.domain.comment.entity;

import java.util.ArrayList;
import java.util.List;

import org.example.pdnight.domain.common.entity.Timestamped;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private Post post;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author_id")
	private User author;

	private String content;

	//부모댓글 - 자기참조
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Comment parent;

	//대댓글 리스트
	@OneToMany(mappedBy = "parent")
	private List<Comment> children = new ArrayList<>();

	//댓글 생성자
	private Comment(Post post, User author, String content) {
		this.post = post;
		this.author = author;
		this.content = content;
	}

	//대댓글 생성자
	private Comment(Post post, User author, String content, Comment parent) {
		this.post = post;
		this.author = author;
		this.content = content;
		this.parent = parent;
	}

	//댓글 생성 메서드
	public static Comment create(Post post, User author, String content) {
		return new Comment(post, author, content);
	}

	//대댓글 생성 메서드
	public static Comment createChild(Post post, User author, String content, Comment parent) {
		return new Comment(post, author, content, parent);
	}

}