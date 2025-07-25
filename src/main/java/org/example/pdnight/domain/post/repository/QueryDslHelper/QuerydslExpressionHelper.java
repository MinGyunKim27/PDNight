package org.example.pdnight.domain.post.repository.QueryDslHelper;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import org.example.pdnight.domain.common.enums.JoinStatus;
import org.example.pdnight.domain.post.entity.QPost;

import java.util.List;

import static org.example.pdnight.domain.hobby.entity.QPostHobby.postHobby;
import static org.example.pdnight.domain.participant.entity.QPostParticipant.postParticipant;
import static org.example.pdnight.domain.techStack.entity.QPostTech.postTech;

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

    public static BooleanExpression isHaveHobby(QPost post, List<Long> hobbyIds) {
        return JPAExpressions
                .select(postHobby.countDistinct())
                .from(postHobby)
                .where(postHobby.post.id.eq(post.id)
                        .and(postHobby.hobby.id.in(hobbyIds)))
                .eq((long) hobbyIds.size());
    }

    public static BooleanExpression isHaveTechStack(QPost post, List<Long> techStackIds) {
        return JPAExpressions
                .select(postTech.countDistinct())
                .from(postTech)
                .where(postTech.post.id.eq(post.id)
                        .and(postTech.techStack.id.in(techStackIds)))
                .eq((long) techStackIds.size());
    }

}
