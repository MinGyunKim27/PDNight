package org.example.pdnight.domain.post.domain.comment;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.global.common.entity.Timestamped;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;

    private Long authorId;

    private String content;

    //대댓글 트리 제한을 위한 필드 : 기본0, 부모가 있을 시 1
    private int depth;

    //부모댓글 - 자기참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    //대댓글 리스트
    @OneToMany(mappedBy = "parent")
    private List<Comment> children = new ArrayList<>();

    //댓글 생성자
    private Comment(Long postId, Long authorId, String content) {
        this.postId = postId;
        this.authorId = authorId;
        this.content = content;
        this.depth = 0;
    }

    //대댓글 생성자
    private Comment(Long postId, Long authorId, String content, Comment parent) {
        this.postId = postId;
        this.authorId = authorId;
        this.content = content;
        this.parent = parent;
        this.depth = 1;
    }

    //댓글 생성 메서드
    public static Comment create(Long postId, Long authorId, String content) {
        return new Comment(postId, authorId, content);
    }

    //대댓글 생성 메서드
    public static Comment createChild(Long postId, Long authorId, String content, Comment parent) {
        if (parent.depth >= 1) {
            throw new BaseException(ErrorCode.INVALID_COMMENT_DEPTH);
        }

        return new Comment(postId, authorId, content, parent);
    }

    //댓글 내용 수정 메서드
    public void updateContent(String content) {
        this.content = content;
    }

}