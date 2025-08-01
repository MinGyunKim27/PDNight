package org.example.pdnight.domain.event.infra;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.event.domain.EventReader;
import org.example.pdnight.domain.event.domain.entity.Event;
import org.example.pdnight.domain.event.domain.entity.EventParticipant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.example.pdnight.domain.event.domain.entity.QEvent.event;
import static org.example.pdnight.domain.event.domain.entity.QEventParticipant.eventParticipant;

@Repository
@RequiredArgsConstructor
public class EventReaderImpl implements EventReader {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Event> findById(Long id) {
        return queryFactory.selectFrom(event)
                .where(event.id.eq(id))
                .stream()
                .findFirst();
    }

    @Override
    public Page<Event> findAllEvent(Pageable pageable) {
        List<Event> content = queryFactory
                .selectFrom(event)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Count 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(event.count())
                .from(event);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);

    }

    @Override
    public boolean existsEventByIdAndUserId(Long eventId, Long userId) {
        Integer existOne = queryFactory
                .selectOne()
                .from(eventParticipant)
                .where(
                        eventParticipant.event.id.eq(eventId),
                        eventParticipant.userId.eq(userId)
                )
                .fetchFirst();

        return existOne != null && existOne > 0;
    }


    // 이벤트에 현재 참가된 인원 수
    @Override
    public Long getEventParticipantByEventId(Long eventId) {
        return queryFactory
                .select(eventParticipant.count())
                .from(eventParticipant)
                .where(eventParticipant.event.id.eq(eventId))
                .fetchFirst();
    }

    // 이벤트에 참가 신청한 유저 목록 조회
    @Override
    public Page<EventParticipant> findByEventWithUser(Event event, Pageable pageable) {
        List<EventParticipant> content = queryFactory
                .selectFrom(eventParticipant)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Count 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(eventParticipant.count())
                .from(eventParticipant)
                .where(eventParticipant.event.id.eq(event.getId()));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);

    }

    @Override
    public Page<Event> getMyParticipantEvents(Long userId, Pageable pageable) {
        List<Event> content = queryFactory
                .selectFrom(event)
                .leftJoin(event.eventParticipants, eventParticipant).fetchJoin()
                .where(eventParticipant.userId.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Count 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(event.count())
                .from(event);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
