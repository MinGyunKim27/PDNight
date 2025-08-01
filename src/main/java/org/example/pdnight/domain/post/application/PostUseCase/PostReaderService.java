package org.example.pdnight.domain.post.application.PostUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.domain.post.PostParticipant;
import org.example.pdnight.domain.post.domain.post.PostReader;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.domain.post.presentation.dto.response.InviteResponseDto;
import org.example.pdnight.domain.post.presentation.dto.response.ParticipantResponse;
import org.example.pdnight.domain.post.presentation.dto.response.PostResponseDto;
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

    // 게시글 단건 조회
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.ONE_POST, key = "#id")
    public PostResponseDto findPost(Long id) {
        Post foundPost = postReader.getPostById(id).orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
        int participants = acceptedParticipantsCounter(foundPost.getPostParticipants());
        return PostResponseDto.toDtoWithCount(foundPost, foundPost.getPostParticipants().size(), participants);
    }

    // 내가 좋아요 누른 게시물 조회
    @Cacheable(
            value = CacheName.LIKED_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #userId}"
    )
    public PagedResponse<PostResponseDto> findMyLikedPosts(Long userId, Pageable pageable) {
        Page<PostResponseDto> myLikePost = postReader.getMyLikePost(userId, pageable);
        return PagedResponse.from(myLikePost);
    }

    // 내 성사된/ 신청한 게시물 조회
    @Cacheable(
            value = CacheName.CONFIRMED_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #userId, #joinStatus}"
    )

    // 내가 참여한 게시물 조회
    public PagedResponse<PostResponseDto> findMyConfirmedPosts(Long userId, JoinStatus joinStatus, Pageable pageable) {
        Page<PostResponseDto> myLikePost = postReader.getConfirmedPost(userId, joinStatus, pageable);
        return PagedResponse.from(myLikePost);
    }

    //내 작성 게시물 조회
    @Cacheable(
            value = CacheName.WRITTEN_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #userId}"
    )
    public PagedResponse<PostResponseDto> findMyWrittenPosts(Long userId, Pageable pageable) {
        Page<Post> postSearch = postReader.getWrittenPost(userId, pageable);
        Page<PostResponseDto> myLikePost = postSearch.map(search -> {
            int participantCount = acceptedParticipantsCounter(search.getPostParticipants());
            return PostResponseDto.toDtoWithCount(search, search.getPostParticipants().size(), participantCount);
        });

        return PagedResponse.from(myLikePost);
    }

    //추천 게시물 조회
    @Cacheable(
            value = CacheName.SUGGESTED_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #userId}"
    )
    public PagedResponse<PostResponseDto> getSuggestedPosts(Long userId, Pageable pageable) {
        Page<Post> postSearch = postReader.getSuggestedPost(userId, pageable);
        Page<PostResponseDto> suggestedPost = postSearch.map(search -> {
            int participantCount = acceptedParticipantsCounter(search.getPostParticipants());
            return PostResponseDto.toDtoWithCount(search, search.getPostParticipants().size(), participantCount);
        });
        return PagedResponse.from(suggestedPost);
    }

    //게시물 조건 검색
    @Transactional(readOnly = true)
    @Cacheable(
            value = CacheName.SEARCH_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #maxParticipants, #ageLimit, #jobCategoryLimit, #genderLimit, #hobbyIdList, #techStackIdList}"
    )
    public PagedResponse<PostResponseDto> getPostDtosBySearch(
            Pageable pageable,
            Integer maxParticipants,
            AgeLimit ageLimit,
            JobCategory jobCategoryLimit,
            Gender genderLimit
    ) {
        Page<Post> postSearch = postReader.findPostsBySearch(pageable, maxParticipants,
                ageLimit, jobCategoryLimit, genderLimit);
        Page<PostResponseDto> postDtosBySearch = postSearch.map(search -> {
            int participantCount = acceptedParticipantsCounter(search.getPostParticipants());
            return PostResponseDto.toDtoWithCount(search, search.getPostParticipants().size(), participantCount);
        });
        return PagedResponse.from(postDtosBySearch);
    }

    // ===========================================validate==========================================================
    // 참가자 목록 조회 용 검증 로직

    private void validateMyPost(Post post, Long loginId) {
        if (!post.getAuthorId().equals(loginId)) {
            throw new BaseException(ErrorCode.NO_VIEWING_PERMISSION);
        }
    }

    private Post getPost(Long postId) {
        return postReader.getPostById(postId)
                .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
    }

    // 리스트를 불러와서 참여자 수
    private int acceptedParticipantsCounter(List<PostParticipant> participants) {
        return (int) participants.stream()
                .filter(participant -> participant.getStatus() == JoinStatus.ACCEPTED)
                .count();
    }

    public PagedResponse<InviteResponseDto> getMyInvited(Long userId, Pageable pageable) {
        Page<InviteResponseDto> myInvited = postReader.getMyInvited(userId, pageable);

        return PagedResponse.from(myInvited);
    }

    public PagedResponse<InviteResponseDto> getMyInvite(Long userId, Pageable pageable) {
        Page<InviteResponseDto> myInvite = postReader.getMyInvite(userId, pageable);

        return PagedResponse.from(myInvite);
    }
}
