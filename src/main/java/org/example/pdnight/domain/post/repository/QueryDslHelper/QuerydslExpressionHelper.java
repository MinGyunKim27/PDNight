package org.example.pdnight.domain.post.repository.QueryDslHelper;

import com.querydsl.core.types.Expression;
import com.querydsl.jpa.JPAExpressions;
import org.example.pdnight.domain.participant.entity.QPostParticipant;
import org.example.pdnight.domain.common.enums.JoinStatus;
import org.example.pdnight.domain.post.entity.QPost;

public class QuerydslExpressionHelper {

    private static final QPostParticipant participant = QPostParticipant.postParticipant;

    public static Expression<Long> participantCount(QPost post) {
        return JPAExpressions
                .select(participant.count())
                .from(participant)
                .where(participant.post.eq(post));
    }

    public static Expression<Long> acceptedParticipantCount(QPost post) {
        return JPAExpressions
                .select(participant.count())
                .from(participant)
                .where(participant.post.eq(post)
                        .and(participant.status.eq(JoinStatus.ACCEPTED)));
    }
}
