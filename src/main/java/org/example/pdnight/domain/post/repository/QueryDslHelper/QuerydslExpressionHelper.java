package org.example.pdnight.domain.post.repository.QueryDslHelper;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import org.example.pdnight.domain.common.enums.JoinStatus;
import org.example.pdnight.domain.post.entity.QPost;

import java.util.List;

import static org.example.pdnight.domain.participant.entity.QPostParticipant.postParticipant;

public class QuerydslExpressionHelper {


    public static Expression<Long> participantCount(QPost post) {
        return JPAExpressions
                .select(postParticipant.count())
                .from(postParticipant)
                .where(postParticipant.post.eq(post));
    }

    public static Expression<Long> acceptedParticipantCount(QPost post) {
        return JPAExpressions
                .select(postParticipant.count())
                .from(postParticipant)
                .where(postParticipant.post.eq(post)
                        .and(postParticipant.status.eq(JoinStatus.ACCEPTED)));
    }
}
