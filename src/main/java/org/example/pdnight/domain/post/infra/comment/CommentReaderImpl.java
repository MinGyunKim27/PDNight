package org.example.pdnight.domain.post.infra.comment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.comment.Comment;
import org.example.pdnight.domain.post.domain.comment.CommentReader;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.example.pdnight.domain.post.domain.comment.QComment.comment;


@Repository
@RequiredArgsConstructor
public class CommentReaderImpl implements CommentReader {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Comment> findByPostIdOrderByIdAsc(Long postId) {
        return jpaQueryFactory
                .select(comment)
                .from(comment)
                .where(comment.postId.eq(postId))
                .fetch();
    }

}