package org.example.pdnight.domain.post.infra.invite;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.invite.InviteReader;
import org.example.pdnight.domain.post.domain.invite.QInvite;
import org.example.pdnight.domain.post.presentation.dto.response.InviteResponseDto;
import org.example.pdnight.domain.post.presentation.dto.response.QInviteResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class InviteReaderImpl implements InviteReader {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<InviteResponseDto> getMyInvited(Long userId, Pageable pageable) {
        QInvite invite = QInvite.invite;

        JPQLQuery<InviteResponseDto> query = queryFactory
                .select(new QInviteResponseDto(
                        invite.id,
                        invite.inviteeId,
                        invite.postId
                ))
                .from(invite)
                .where(invite.inviteeId.eq(userId)) // 내가 초대 받은 경우
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        JPQLQuery<Long> countQuery = queryFactory
                .select(invite.count())
                .from(invite)
                .where(invite.inviteeId.eq(userId));

        return PageableExecutionUtils.getPage(query.fetch(), pageable, countQuery::fetchOne);
    }

    @Override
    public Page<InviteResponseDto> getMyInvite(Long userId, Pageable pageable) {
        QInvite invite = QInvite.invite;

        JPQLQuery<InviteResponseDto> query = queryFactory
                .select(new QInviteResponseDto(
                        invite.id,
                        invite.inviteeId,
                        invite.postId
                ))
                .from(invite)
                .where(invite.inviterId.eq(userId)) // 내가 초대한 경우
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        JPQLQuery<Long> countQuery = queryFactory
                .select(invite.count())
                .from(invite)
                .where(invite.inviterId.eq(userId));

        return PageableExecutionUtils.getPage(query.fetch(), pageable, countQuery::fetchOne);
    }
}
