package org.example.pdnight.domain1.post.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain1.common.enums.JobCategory;
import org.example.pdnight.domain1.common.enums.JoinStatus;
import org.example.pdnight.domain1.post.dto.response.PostResponseWithApplyStatusDto;
import org.example.pdnight.domain1.post.dto.response.PostWithJoinStatusAndAppliedAtResponseDto;
import org.example.pdnight.domain1.post.dto.response.QPostResponseWithApplyStatusDto;
import org.example.pdnight.domain1.post.dto.response.QPostWithJoinStatusAndAppliedAtResponseDto;
import org.example.pdnight.domain1.post.entity.Post;
import org.example.pdnight.domain1.post.enums.AgeLimit;
import org.example.pdnight.domain1.post.enums.Gender;
import org.example.pdnight.domain1.post.enums.PostStatus;
import org.example.pdnight.domain1.post.repository.QueryDslHelper.QuerydslExpressionHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.example.pdnight.domain1.hobby.entity.QHobby.hobby1;
import static org.example.pdnight.domain1.hobby.entity.QPostHobby.postHobby;
import static org.example.pdnight.domain1.participant.entity.QPostParticipant.postParticipant;
import static org.example.pdnight.domain1.post.entity.QPost.post;
import static org.example.pdnight.domain1.postLike.entity.QPostLike.postLike;
import static org.example.pdnight.domain1.techStack.entity.QPostTech.postTech;
import static org.example.pdnight.domain1.techStack.entity.QTechStack.techStack1;

@Repository
@RequiredArgsConstructor
public class PostRepositoryQueryImpl implements PostRepositoryQuery {
    private final JPAQueryFactory queryFactory;

    // 게시글 단건 조회
    @Override
    public PostResponseWithApplyStatusDto getOpenedPostById(Long postId) {
        PostResponseWithApplyStatusDto content = queryFactory
                .select(postResponseDtoProjection())
                .from(post)
                .where(post.id.eq(postId)
                        .and(post.status.eq(PostStatus.OPEN))) // OPEN 상태만 조회
                .fetchOne();

        // null 이면 추가 조회 막기
        if (content == null) return null;
        // dto 에 추가 : 취미, 기술스택
        mappingToDtoWithList(postId, content);

        return content;
    }

    // 닫힌 상태가 아닌 게시글 단건 조회
    @Override
    public Optional<Post> getPostByIdNotClose(Long postId) {

        Post findPost = queryFactory
                .select(post)
                .from(post)
                .where(post.id.eq(postId)
                        .and(post.status.ne(PostStatus.CLOSED))) // OPEN 상태만 조회
                .fetchOne();

        return Optional.ofNullable(findPost);
    }


    // 내 좋아요 게시글 목록 조회
    @Override
    public Page<PostResponseWithApplyStatusDto> getMyLikePost(Long userId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        //닫힌 상태가 아닐 때
        builder.and(post.status.ne(PostStatus.CLOSED));
        builder.and(postLike.user.id.eq(userId));

        List<PostResponseWithApplyStatusDto> contents = queryFactory
                .select(postResponseDtoProjection())
                .from(post)
                .join(postLike).on(postLike.post.eq(post))
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // dto 에 추가 : 취미, 기술스택
        mappingToDtoListWithList(contents);

        Long count = queryFactory
                .select(post.count())
                .from(post)
                .join(postLike).on(postLike.post.eq(post))
                .where(builder)
                .fetchOne();

        return PageableExecutionUtils.getPage(contents, pageable, () -> Optional.ofNullable(count).orElse(0L));
    }


    // 참여 신청한 게시글 목록 조회
    @Override
    public Page<PostWithJoinStatusAndAppliedAtResponseDto> getConfirmedPost(Long userId, JoinStatus joinStatus, Pageable pageable) {
        QPostWithJoinStatusAndAppliedAtResponseDto qPostWithStatusResponseDto = new QPostWithJoinStatusAndAppliedAtResponseDto(
                post.id,
                post.author.id,
                post.title,
                post.timeSlot,
                post.publicContent,
                post.privateContent,
                post.status,
                post.maxParticipants,
                post.genderLimit,
                post.jobCategoryLimit,
                post.ageLimit,
                postParticipant.status,
                postParticipant.createdAt);

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(postParticipant.user.id.eq(userId));
        //닫힌 상태가 아닐 때
        builder.and(post.status.ne(PostStatus.CLOSED));

        if (joinStatus != null) {
            builder.and(postParticipant.status.eq(joinStatus));
        }


        List<PostWithJoinStatusAndAppliedAtResponseDto> content = queryFactory
                .select(qPostWithStatusResponseDto)
                .from(post)
                .join(postParticipant).on(postParticipant.post.eq(post))
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(post.count())
                .from(post)
                .join(postParticipant).on(postParticipant.post.eq(post))
                .where(builder)
                .fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> Optional.ofNullable(count).orElse(0L));
    }


    // 게시글 검색 목록 조회
    @Override
    public Page<PostResponseWithApplyStatusDto> findPostDtosBySearch(
            Pageable pageable,
            Integer maxParticipants,
            AgeLimit ageLimit,
            JobCategory jobCategoryLimit,
            Gender genderLimit,
            List<Long> hobbyIds,
            List<Long> techStackIds
    ) {
        // 조건 누적용 BooleanBuilder
        BooleanBuilder builder = new BooleanBuilder();
        // 무조건 포함되는 조건
        builder.and(post.status.eq(PostStatus.OPEN));
        // nullable 조건 추가
        if (maxParticipants != null) {
            builder.and(post.maxParticipants.goe(maxParticipants));
        }
        if (ageLimit != null) {
            builder.and(post.ageLimit.eq(ageLimit));
        }
        if (jobCategoryLimit != null) {
            builder.and(post.jobCategoryLimit.eq(jobCategoryLimit));
        }
        if (genderLimit != null) {
            builder.and(post.genderLimit.eq(genderLimit));
        }
        if (hobbyIds != null && !hobbyIds.isEmpty()) {
            builder.and(QuerydslExpressionHelper.isHaveHobby(post, hobbyIds));
        }
        if (techStackIds != null && !techStackIds.isEmpty()) {
            builder.and(QuerydslExpressionHelper.isHaveTechStack(post, techStackIds));
        }

        List<PostResponseWithApplyStatusDto> contents = queryFactory
                .select(postResponseDtoProjection())
                .from(post)
                .leftJoin(post.author)
                .leftJoin(post.postHobbies, postHobby)
                .leftJoin(postHobby.hobby, hobby1)
                .leftJoin(post.postTechs, postTech)
                .leftJoin(postTech.techStack, techStack1)
                .where(builder)
                .groupBy(post.id)
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // dto 에 추가 : 취미, 기술스택
        mappingToDtoListWithList(contents);

        Long total = Optional.ofNullable(
                queryFactory
                        .select(post.countDistinct())
                        .from(post)
                        .leftJoin(post.author)
                        .leftJoin(post.postHobbies, postHobby)
                        .leftJoin(postHobby.hobby, hobby1)
                        .leftJoin(post.postTechs, postTech)
                        .leftJoin(postTech.techStack, techStack1)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(contents, pageable, total);
    }


    // 내가 작성 한 게시물 조회
    @Override
    public Page<PostResponseWithApplyStatusDto> getWrittenPost(
            Long userId,
            Pageable pageable
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(post.author.id.eq(userId));
        builder.and(post.status.ne(PostStatus.CLOSED));

        List<PostResponseWithApplyStatusDto> contents = queryFactory
                .select(postResponseDtoProjection())
                .from(post)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // dto 에 추가 : 취미, 기술스택
        mappingToDtoListWithList(contents);

        Long count = queryFactory
                .select(post.count())
                .from(post)
                .where(builder)
                .fetchOne();

        return PageableExecutionUtils.getPage(contents, pageable, () -> Optional.ofNullable(count).orElse(0L));
    }

    // 추천 게시글 목록 조회
    @Override
    public Page<PostResponseWithApplyStatusDto> getSuggestedPost(Long userId, Pageable pageable) {
        List<PostResponseWithApplyStatusDto> contents = queryFactory
                .select(postResponseDtoProjection())
                .from(post)
                .leftJoin(postLike).on(postLike.post.eq(post)) // 좋아요 조인
                .groupBy(post.id)
                .where(postLike.user.id.eq(userId).and(post.status.eq(PostStatus.OPEN)))
                .orderBy(postLike.count().desc()) // 좋아요 수 내림차순 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // dto 에 추가 : 취미, 기술스택
        mappingToDtoListWithList(contents);

        Long count = queryFactory
                .select(post.countDistinct())
                .from(post)
                .leftJoin(postLike).on(postLike.post.eq(post))
                .where(postLike.user.id.eq(userId))
                .fetchOne();

        return PageableExecutionUtils.getPage(contents, pageable, () -> Optional.ofNullable(count).orElse(0L));
    }

    // -- HELPER 메서드 -- //

    // DTO 프로젝션 : 참여자 수, 신청자 수
    private QPostResponseWithApplyStatusDto postResponseDtoProjection() {
        return new QPostResponseWithApplyStatusDto(
                post.id,
                post.author.id,
                post.title,
                post.timeSlot,
                post.publicContent,
                post.privateContent,
                post.status,
                post.maxParticipants,
                post.genderLimit,
                post.jobCategoryLimit,
                post.ageLimit,
                QuerydslExpressionHelper.participantCount(post),
                QuerydslExpressionHelper.acceptedParticipantCount(post),
                post.createdAt,
                post.updatedAt
        );
    }

    // List 형으로 추가 정보가 필요할 때 Dto 매핑 : 단건 조회 시
    private void mappingToDtoWithList(Long postId, PostResponseWithApplyStatusDto content) {
        List<String> hobbyList = queryFactory
                .select(hobby1.hobby)
                .from(postHobby)
                .join(postHobby.hobby, hobby1)
                .where(postHobby.post.id.in(postId))
                .fetch();

        List<String> techList = queryFactory
                .select(techStack1.techStack)
                .from(postTech)
                .join(postTech.techStack, techStack1)
                .where(postTech.post.id.in(postId))
                .fetch();

        // DTO에 매핑
        content.setHobbyAndTech(hobbyList, techList);
    }

    // List 형으로 추가 정보가 필요할 때 Dto 매핑 : 목록 조회 시
    private void mappingToDtoListWithList(List<PostResponseWithApplyStatusDto> contents) {
        if (contents == null || contents.isEmpty()) return;
        // 게시글 ID 리스트 추출
        List<Long> postIds = contents.stream().map(PostResponseWithApplyStatusDto::getPostId).toList();
        // 취미/기술스택 맵 조회
        Map<Long, List<String>> hobbyMap = getPostHobbyMap(postIds);
        Map<Long, List<String>> techStackMap = getPostTechMap(postIds);
        // 각 DTO에 매핑
        for (PostResponseWithApplyStatusDto dto : contents) {
            List<String> hobbyList = hobbyMap.getOrDefault(dto.getPostId(), Collections.emptyList());
            List<String> techList = techStackMap.getOrDefault(dto.getPostId(), Collections.emptyList());
            dto.setHobbyAndTech(hobbyList, techList);
        }
    }


    private Map<Long, List<String>> getPostHobbyMap(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) return Collections.emptyMap();
        return queryFactory
                .select(postHobby.post.id, hobby1.hobby)
                .from(postHobby)
                .join(postHobby.hobby, hobby1)
                .where(postHobby.post.id.in(postIds))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(postHobby.post.id),
                        Collectors.mapping(tuple -> tuple.get(hobby1.hobby), Collectors.toList())
                ));
    }

    private Map<Long, List<String>> getPostTechMap(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) return Collections.emptyMap();
        return queryFactory
                .select(postTech.post.id, techStack1.techStack)
                .from(postTech)
                .join(postTech.techStack, techStack1)
                .where(postTech.post.id.in(postIds))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(postTech.post.id),
                        Collectors.mapping(tuple -> tuple.get(techStack1.techStack), Collectors.toList())
                ));
    }
}
