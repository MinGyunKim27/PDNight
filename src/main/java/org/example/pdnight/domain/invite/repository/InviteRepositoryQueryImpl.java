package org.example.pdnight.domain.invite.repository;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.invite.dto.response.InviteResponseDto;
import org.example.pdnight.domain.invite.dto.response.QInviteResponseDto;
import org.example.pdnight.domain.invite.entity.QInvite;
import org.example.pdnight.domain.post.entity.QPost;
import org.example.pdnight.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InviteRepositoryQueryImpl implements InviteRepositoryQuery{
    private final JPAQueryFactory queryFactory;

    //내가 초대 받은 목록
    @Override
    public Page<InviteResponseDto> getMyInvited(Long userId, Pageable pageable) {
        QInvite invite = QInvite.invite;
        QUser user = QUser.user;
        QPost post = QPost.post;

        JPQLQuery<InviteResponseDto> query = queryFactory
                .select(new QInviteResponseDto(
                        invite.id,
                        invite.invitee.id,
                        invite.invitee.nickname,
                        invite.post.id,
                        invite.post.title
                ))
                .from(invite)
                .join(invite.invitee, user).fetchJoin()
                .join(invite.post, post).fetchJoin()
                .where(invite.invitee.id.eq(userId)) // 내가 초대 받은 경우
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        JPQLQuery<Long> countQuery = queryFactory
                .select(invite.count())
                .from(invite)
                .where(invite.invitee.id.eq(userId));

        return PageableExecutionUtils.getPage(query.fetch(), pageable, countQuery::fetchOne);
    }

    //내가 초대 한 목록
    @Override
    public Page<InviteResponseDto> getMyInvite(Long userId, Pageable pageable) {
        QInvite invite = QInvite.invite;
        QUser user = QUser.user;
        QPost post = QPost.post;

        JPQLQuery<InviteResponseDto> query = queryFactory
                .select(new QInviteResponseDto(
                        invite.id,
                        invite.invitee.id,
                        invite.invitee.nickname,
                        invite.post.id,
                        invite.post.title
                ))
                .from(invite)
                .join(invite.invitee, user).fetchJoin()
                .join(invite.post, post).fetchJoin()
                .where(invite.inviter.id.eq(userId)) // 내가 초대한 경우
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        JPQLQuery<Long> countQuery = queryFactory
                .select(invite.count())
                .from(invite)
                .where(invite.inviter.id.eq(userId));

        return PageableExecutionUtils.getPage(query.fetch(), pageable, countQuery::fetchOne);
    }
}
