package org.example.pdnight.domain.post.application.PostUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.post.domain.post.PostDocument;
import org.example.pdnight.domain.post.domain.post.PostParticipantDocument;
import org.example.pdnight.domain.post.domain.post.PostReader;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.domain.post.presentation.dto.response.InviteResponse;
import org.example.pdnight.domain.post.presentation.dto.response.ParticipantResponse;
import org.example.pdnight.domain.post.presentation.dto.response.PostResponse;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.enums.JobCategory;
import org.example.pdnight.global.common.exception.BaseException;
import org.example.pdnight.global.constant.CacheName;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class PostReaderESService {

    private final PostReader postReader;

    //region 게시글 조회
    // 게시글 단건 조회 O
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.ONE_POST, key = "#id")
    public PostResponse findPostES(Long id) {
        PostDocument foundPost = getPostES(id);

        int participants = (int) foundPost.getPostParticipants().stream()
                .filter(participant -> participant.getStatus() == JoinStatus.ACCEPTED)
                .count();
        return PostResponse.toDtoWithCountES(foundPost, participants, foundPost.getPostParticipants().size());
    }

    //게시물 조건 검색 O
    @Transactional(readOnly = true)
    @Cacheable(
            value = CacheName.SEARCH_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #maxParticipants, #ageLimit, #jobCategoryLimit, #genderLimit}"
    )
    public PagedResponse<PostResponse> getPostDtosBySearchES(
            Pageable pageable,
            Integer maxParticipants,
            AgeLimit ageLimit,
            JobCategory jobCategoryLimit,
            Gender genderLimit
    ) {
        Page<PostDocument> postSearch = postReader.findPostsBySearchES(pageable, maxParticipants,
                ageLimit, jobCategoryLimit, genderLimit);
        Page<PostResponse> postDtosBySearch = postSearch.map(search -> {
            int participantCount = acceptedParticipantsCounter(search.getPostParticipants());
            log.info("participantCount : {}", participantCount);
            log.info("search.getPostParticipants().size() : {}", search.getPostParticipants().size());
            return PostResponse.toDtoWithCountES(search, participantCount, search.getPostParticipants().size());
        });
        return PagedResponse.from(postDtosBySearch);
    }

    // 내 성사된/ 신청한 게시물 조회 -> 상태가 입력 안되는 경우만 에러
    @Cacheable(
            value = CacheName.CONFIRMED_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #userId, #joinStatus}"
    )
    public PagedResponse<PostResponse> findMyConfirmedPostsES(Long userId, JoinStatus joinStatus, Pageable pageable) {
        Page<PostDocument> myLikePost = postReader.getConfirmedPostES(userId, joinStatus, pageable);
        Page<PostResponse> result = myLikePost.map(PostResponse::toDtoES);
        return PagedResponse.from(result);
    }

    //내 작성 게시물 조회 O
    @Cacheable(
            value = CacheName.WRITTEN_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #userId}"
    )
    public PagedResponse<PostResponse> findMyWrittenPostsES(Long userId, Pageable pageable) {
        Page<PostDocument> postSearch = postReader.getWrittenPostES(userId, pageable);
        Page<PostResponse> myLikePost = postSearch.map(search -> {
            int participantCount = acceptedParticipantsCounter(search.getPostParticipants());
            return PostResponse.toDtoWithCountES(search, participantCount, search.getPostParticipants().size());
        });

        return PagedResponse.from(myLikePost);
    }

    // 내가 좋아요 누른 게시물 조회 O
    @Cacheable(
            value = CacheName.LIKED_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #userId}"
    )
    public PagedResponse<PostResponse> findMyLikedPostsES(Long userId, Pageable pageable) {
        Page<PostDocument> myLikePost = postReader.getMyLikePostES(userId, pageable);
        Page<PostResponse> result = myLikePost.map(PostResponse::toDtoES);
        return PagedResponse.from(result);
    }
    //endregion

    //region 신청자목록조회
    //본인이 작성한 게시글 신청자 목록 조회  O
    public PagedResponse<ParticipantResponse> getParticipantListByPendingES(Long loginId, Long postId, int page, int size) {
        PostDocument post = getPostES(postId);

        // 신청자 조회 안됨 : 게시글이 본인것이 아님
        validateMyPost(post, loginId);

        List<ParticipantResponse> pendingParticipants = post
                .getPostParticipants()
                .stream()
                .filter(postParticipant -> postParticipant.getStatus() == JoinStatus.PENDING)
                .map(p -> ParticipantResponse.from(p.getUserId(), p.getPostId(), p.getStatus(), p.getCreatedAt(), p.getUpdatedAt()))
                .toList();

        Pageable pageable = PageRequest.of(page, size);

        Page<ParticipantResponse> pagedPendingParticipants = new PageImpl<>(pendingParticipants, pageable, pendingParticipants.size());

        return PagedResponse.from(pagedPendingParticipants);
    }

    //수락한 참가자 조회 O createdAt, updatedAt
    public PagedResponse<ParticipantResponse> getParticipantListByAcceptedES(Long loginId, Long postId, int page, int size) {
        PostDocument post = getPostES(postId);

        // 신청자 조회 안됨 : 게시글이 본인것이 아님
        validateMyPost(post, loginId);

        List<ParticipantResponse> pendingParticipants = post
                .getPostParticipants()
                .stream()
                .filter(postParticipant -> postParticipant.getStatus() == JoinStatus.ACCEPTED)
                .map(p -> ParticipantResponse.from(p.getUserId(), p.getPostId(), p.getStatus(), p.getCreatedAt(), p.getUpdatedAt()))
                .toList();

        Pageable pageable = PageRequest.of(page, size);

        Page<ParticipantResponse> pagedPendingParticipants = new PageImpl<>(pendingParticipants, pageable, pendingParticipants.size());

        return PagedResponse.from(pagedPendingParticipants);
    }

    //endregion
    //region 게시물초대 조회
    // 초대 받은 게시물 조회 O
    public PagedResponse<InviteResponse> getMyInvitedES(Long userId, Pageable pageable) {
        Page<PostDocument> myInvited = postReader.getMyInvitedES(userId, pageable);
        Page<InviteResponse> result = myInvited.map(post -> InviteResponse.from(post.getAuthorId(), post.getId()));
        return PagedResponse.from(result);
    }

    //내가 보낸 초대 조회 O
    public PagedResponse<InviteResponse> getMyInviteES(Long userId, Pageable pageable) {
        List<PostDocument> myInvites = postReader.getMyInviteES(userId);
        List<InviteResponse> list = new ArrayList<>();

        myInvites.stream()
                .filter(post -> post.getInvites() != null)
                .flatMap(post -> post.getInvites().stream())
                .filter(invite -> userId.equals(invite.getInviterId()))
                .forEach(i -> list.add(InviteResponse.from(i.getInviteeId(), i.getPostId())));

        PageImpl<InviteResponse> inviteResponses = new PageImpl<>(list, pageable, list.size());
        return PagedResponse.from(inviteResponses);
    }
    //endregion
    //region 헬퍼메서드
    // 참가자 목록 조회 용 검증 로직

    private void validateMyPost(PostDocument post, Long loginId) {
        if (!post.getAuthorId().equals(loginId)) {
            throw new BaseException(ErrorCode.NO_VIEWING_PERMISSION);
        }
    }

    private PostDocument getPostES(Long postId) {
        PostDocument post = postReader.findByIdES(postId)
                .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));

        if(post.getIsDeleted() != null && post.getIsDeleted()) {
            throw new BaseException(ErrorCode.POST_DEACTIVATED);
        }

        return post;
    }

    // 리스트를 불러와서 참여자 수
    private int acceptedParticipantsCounter(List<PostParticipantDocument> participants) {
        return (int) participants.stream()
                .filter(participant -> participant.getStatus() == JoinStatus.ACCEPTED)
                .count();
    }
    //endregion

}
