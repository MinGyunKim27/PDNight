package org.example.pdnight.domain.post.application.PostUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.domain.post.PostParticipant;
import org.example.pdnight.domain.post.domain.post.PostReader;
import org.example.pdnight.domain.post.domain.post.PostTag;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostReaderService {

    private final PostReader postReader;
    private final TagPort tagPort;

    //region 게시글 조회
    // 게시글 단건 조회
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    @Cacheable(value = CacheName.ONE_POST, key = "#id")
    public PostResponse findPost(Long id) {
        Post foundPost = getPost(id);
        // 참여자 수 조회
        int participants = acceptedParticipantsCounter(foundPost.getPostParticipants());

        // 태그 리스트 조회 - 게시글의 태그 Id 들로 이름 검색
        List<Long> tagIdList = foundPost.getPostTagList().stream().map(PostTag::getTagId).toList();
        List<String> tagNames = tagPort.findAllTagNames(tagIdList);

        return PostResponse.toDtoWithCount(foundPost, tagNames, participants, foundPost.getPostParticipants().size());
    }

    //게시물 조건 검색
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    @Cacheable(
            value = CacheName.SEARCH_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #maxParticipants, #ageLimit, #jobCategoryLimit, #genderLimit}"
    )
    public PagedResponse<PostResponse> getPostDtosBySearch(
            Pageable pageable,
            Integer maxParticipants,
            AgeLimit ageLimit,
            JobCategory jobCategoryLimit,
            Gender genderLimit
    ) {
        Page<Post> postSearch = postReader.findPostsBySearch(pageable, maxParticipants,
                ageLimit, jobCategoryLimit, genderLimit);

        // Page<PostResponse> 매핑
        Page<PostResponse> postDtosBySearch = postSearch.map(search -> {
            // 태그 리스트 조회 - 게시글의 태그 Id 들로 이름 검색
            List<Long> tagIds = search.getPostTagList().stream().map(PostTag::getTagId).toList();
            List<String> tagNames = tagPort.findAllTagNames(tagIds);

            // 참여자 수 조회
            int participantCount = acceptedParticipantsCounter(search.getPostParticipants());
            return PostResponse.toDtoWithCount(search, tagNames, participantCount, search.getPostParticipants().size());
        });
        return PagedResponse.from(postDtosBySearch);
    }

    // 내 성사된/ 신청한 게시물 조회
    @Cacheable(
            value = CacheName.CONFIRMED_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #userId, #joinStatus}"
    )
    public PagedResponse<PostResponse> findMyConfirmedPosts(Long userId, JoinStatus joinStatus, Pageable pageable) {
        Page<PostResponse> myLikePost = postReader.getConfirmedPost(userId, joinStatus, pageable);
        return PagedResponse.from(myLikePost);
    }

    //내 작성 게시물 조회
    @Cacheable(
            value = CacheName.WRITTEN_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #userId}"
    )
    public PagedResponse<PostResponse> findMyWrittenPosts(Long userId, Pageable pageable) {
        Page<Post> postSearch = postReader.getWrittenPost(userId, pageable);

        // Page<PostResponse> 매핑
        Page<PostResponse> myLikePost = postSearch.map(search -> {
            // 태그 리스트 조회 - 게시글의 태그 Id 들로 이름 검색
            List<Long> tagIds = search.getPostTagList().stream().map(PostTag::getTagId).toList();
            List<String> tagNames = tagPort.findAllTagNames(tagIds);

            // 참여자 수 조회
            int participantCount = acceptedParticipantsCounter(search.getPostParticipants());
            return PostResponse.toDtoWithCount(search, tagNames, participantCount, search.getPostParticipants().size());
        });

        return PagedResponse.from(myLikePost);
    }

    //추천 게시물 조회
    @Cacheable(
            value = CacheName.SUGGESTED_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #userId}"
    )
    public PagedResponse<PostResponse> getSuggestedPosts(Long userId, Pageable pageable) {
        Page<Post> postSearch = postReader.getSuggestedPost(userId, pageable);

        // Page<PostResponse> 매핑
        Page<PostResponse> suggestedPost = postSearch.map(search -> {
            // 태그 리스트 조회 - 게시글의 태그 Id 들로 이름 검색
            List<Long> tagIds = search.getPostTagList().stream().map(PostTag::getTagId).toList();
            List<String> tagNames = tagPort.findAllTagNames(tagIds);

            // 참여자 수 조회
            int participantCount = acceptedParticipantsCounter(search.getPostParticipants());
            return PostResponse.toDtoWithCount(search, tagNames, participantCount, search.getPostParticipants().size());
        });
        return PagedResponse.from(suggestedPost);
    }

    // 내가 좋아요 누른 게시물 조회
    @Cacheable(
            value = CacheName.LIKED_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #userId}"
    )
    public PagedResponse<PostResponse> findMyLikedPosts(Long userId, Pageable pageable) {
        Page<PostResponse> myLikePost = postReader.getMyLikePost(userId, pageable);
        return PagedResponse.from(myLikePost);
    }
    //endregion

    //region 신청자목록조회
    //본인이 작성한 게시글 신청자 목록 조회
    public PagedResponse<ParticipantResponse> getParticipantListByPending(Long loginId, Long postId, int page, int size) {
        Post post = getPost(postId);

        // 신청자 조회 안됨 : 게시글이 본인것이 아님
        validateMyPost(post, loginId);

        List<ParticipantResponse> pendingParticipants = post
                .getPostParticipants()
                .stream()
                .filter(postParticipant -> postParticipant.getStatus() == JoinStatus.PENDING)
                .map(ParticipantResponse::toDto)
                .toList();

        Pageable pageable = PageRequest.of(page, size);

        Page<ParticipantResponse> pagedPendingParticipants = new PageImpl<>(pendingParticipants, pageable, pendingParticipants.size());

        return PagedResponse.from(pagedPendingParticipants);
    }

    //수락한 참가자 조회
    public PagedResponse<ParticipantResponse> getParticipantListByAccepted(Long loginId, Long postId, int page, int size) {
        Post post = getPost(postId);

        // 신청자 조회 안됨 : 게시글이 본인것이 아님
        validateMyPost(post, loginId);

        List<ParticipantResponse> pendingParticipants = post
                .getPostParticipants()
                .stream()
                .filter(postParticipant -> postParticipant.getStatus() == JoinStatus.ACCEPTED)
                .map(ParticipantResponse::toDto)
                .toList();

        Pageable pageable = PageRequest.of(page, size);

        Page<ParticipantResponse> pagedPendingParticipants = new PageImpl<>(pendingParticipants, pageable, pendingParticipants.size());

        return PagedResponse.from(pagedPendingParticipants);
    }

    //endregion
    //region 게시물초대 조회
    public PagedResponse<InviteResponse> getMyInvited(Long userId, Pageable pageable) {
        Page<InviteResponse> myInvited = postReader.getMyInvited(userId, pageable);

        return PagedResponse.from(myInvited);
    }

    public PagedResponse<InviteResponse> getMyInvite(Long userId, Pageable pageable) {
        Page<InviteResponse> myInvite = postReader.getMyInvite(userId, pageable);

        return PagedResponse.from(myInvite);
    }
    //endregion
    //region 헬퍼메서드
    // 참가자 목록 조회 용 검증 로직

    private void validateMyPost(Post post, Long loginId) {
        if (!post.getAuthorId().equals(loginId)) {
            throw new BaseException(ErrorCode.NO_VIEWING_PERMISSION);
        }
    }

    private Post getPost(Long postId) {
        Post post = postReader.findByIdWithParticipants(postId)
                .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));

        if (post.getIsDeleted()) {
            throw new BaseException(ErrorCode.POST_DEACTIVATED);
        }

        return post;
    }

    // 리스트를 불러와서 참여자 수
    private int acceptedParticipantsCounter(List<PostParticipant> participants) {
        return (int) participants.stream()
                .filter(participant -> participant.getStatus() == JoinStatus.ACCEPTED)
                .count();
    }
    //endregion

}
