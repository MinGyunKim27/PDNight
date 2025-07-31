package org.example.pdnight.domain.post.application.PostUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.domain.post.PostCommandQuery;

import org.example.pdnight.domain.post.domain.post.PostLike;
import org.example.pdnight.domain.post.domain.post.PostParticipant;
import org.example.pdnight.domain.post.enums.*;
import org.example.pdnight.domain.post.presentation.dto.request.PostRequestDto;
import org.example.pdnight.domain.post.presentation.dto.request.PostStatusRequestDto;
import org.example.pdnight.domain.post.presentation.dto.request.PostUpdateRequestDto;
import org.example.pdnight.domain.post.presentation.dto.response.*;
import org.example.pdnight.domain1.common.dto.PagedResponse;
import org.example.pdnight.domain1.common.enums.ErrorCode;
import org.example.pdnight.domain1.common.enums.JobCategory;
import org.example.pdnight.domain1.common.exception.BaseException;
import org.example.pdnight.global.aop.DistributedLock;
import org.example.pdnight.global.constant.CacheName;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostCommandService {

    private final PostCommandQuery postCommandQuery;

    //참가 신청
    @Transactional
    @CacheEvict(value = CacheName.CONFIRMED_POST, allEntries = true)
    @DistributedLock(
            key = "#postId",
            timeoutMs = 5000
    )
    public ParticipantResponse applyParticipant(Long loginId, Long postId) {
        Post post = getPostWithOpen(postId);

        // 신청 안되는지 확인
        validForCreateParticipant(loginId, post);

        //선착순 포스트인 경우
        PostParticipant participant = isFirstProcess(post, user);

        // 정상 신청
        postCommandQuery.save(participant);

        return ParticipantResponse.from(
                loginId,
                postId,
                participant.getStatus(),
                participant.getCreatedAt(),
                participant.getUpdatedAt()
        );
    }

    //참가 취소
    @Transactional
    @DistributedLock(
            key = "#postId",
            timeoutMs = 5000
    )
    public void deleteParticipant(Long loginId, Long postId) {
        User user = getUser(loginId);
        Post post = getPostWithOpen(postId);

        PostParticipant pending = getParticipantByStatus(user, post, JoinStatus.PENDING);

        // 삭제 안됨 : 신청하지 않거나, 이미 수락 혹은 거절당했을때
        if (pending == null) {
            throw new BaseException(ErrorCode.CANNOT_CANCEL);
        }

        // 정상 삭제
        postCommandQuery.delete(pending);
    }

    //참가 확정(작성자)
    @Transactional
    @CacheEvict(value = CacheName.CONFIRMED_POST, allEntries = true)
    @DistributedLock(
            key = "#postId",
            timeoutMs = 5000
    )
    public ParticipantResponse changeStatusParticipant(Long authorId, Long userId, Long postId, String status) {
        User user = getUser(userId);
        Post post = getPostWithOpen(postId);
        JoinStatus joinStatus = JoinStatus.of(status);

        PostParticipant pending = getParticipantByStatus(user, post, JoinStatus.PENDING);

        // 상태 변경할 수 있는지 확인
        validForChangeStatusParticipant(authorId, post, pending, joinStatus);

        // 상태변경
        pending.changeStatus(joinStatus);

        // 게시글 인원이 꽉차게 되면 게시글 상태를 마감으로 변경 (CONFIRMED)
        changePostStatusForConfirmed(postId, post);

        return ParticipantResponse.from(
                userId,
                postId,
                pending.getStatus(),
                pending.getCreatedAt(),
                pending.getUpdatedAt()
        );
    }

    @CacheEvict(value = CacheName.LIKED_POST, allEntries = true)
    @Transactional
    public PostLikeResponse addLike(Long id, Long userId) {

        User user = getUserByIdOrElseThrow(userId);
        Post post = getPostByIdOrElseThrow(id);

        //좋아요 존재 하면 에러
        validateExists(post, user);

        PostLike postLike = PostLike.create(post, user);
        post.addLike(postLike);
        postCommandQuery.save(postLike);

        return PostLikeResponse.from(postLike);
    }

    @CacheEvict(value = CacheName.LIKED_POST, allEntries = true)
    @Transactional
    public void removeLike(Long id, Long userId) {

        User user = getUserByIdOrElseThrow(userId);
        Post post = getPostByIdOrElseThrow(id);
        PostLike like = getPostLikePostAndUser(post, user);

        post.removeLike(like);
        postCommandQuery.delete(like);
    }

    private static AgeLimit determineAgeLimit(Long age) {
        if (age >= 20 && age < 30) return AgeLimit.AGE_20S;
        else if (age >= 30 && age < 40) return AgeLimit.AGE_30S;
        else if (age >= 40 && age < 50) return AgeLimit.AGE_40S;
        else if (age >= 50) return AgeLimit.AGE_50S;
        return AgeLimit.ALL;
    }

    // 참가 요건 확인 (신청 할 때 validate)
    private void validForCreateParticipant(Long userId, Post post) {

        // 신청 안됨 : 본인 게시글에 본인이 신청하는 경우
        if (post.getAuthorId().equals(userId)) {
            throw new BaseException(ErrorCode.CANNOT_PARTICIPATE_SELF);
        }

        //나이 조건 안 맞으면 신청 불가
        AgeLimit userAgeLimit = determineAgeLimit(user.getAge());
        if (post.getAgeLimit() != AgeLimit.ALL && post.getAgeLimit() != userAgeLimit) {
            throw new BaseException(ErrorCode.AGE_LIMIT_NOT_SATISFIED);
        }

        //성별 조건 안 맞으면 신청 불가
        if (post.getGenderLimit() != Gender.ALL && post.getGenderLimit() != user.getGender()) {
            throw new BaseException(ErrorCode.GENDER_LIMIT_NOT_SATISFIED);
        }

        //직업군 조건 안 맞으면 신청 불가
        if (post.getJobCategoryLimit() != JobCategory.ALL && post.getJobCategoryLimit() != user.getJobCategory()) {
            throw new BaseException(ErrorCode.JOB_CATEGORY_LIMIT_NOT_SATISFIED);
        }

        // 신청 안됨 : 이미 신청함 - JoinStatus status 가 대기중 or 수락됨 인 경우
        PostParticipant pending = getParticipantByStatus(user, post, JoinStatus.PENDING);
        PostParticipant accepted = getParticipantByStatus(user, post, JoinStatus.ACCEPTED);

        if (pending != null) {
            throw new BaseException(ErrorCode.POST_ALREADY_PENDING);
        }
        if (accepted != null) {
            throw new BaseException(ErrorCode.POST_ALREADY_ACCEPTED);
        }
    }

    // 상태 변경 가능 확인 (상태가 변경될 때 validate)
    private void validForChangeStatusParticipant(Long authorId, Post post, PostParticipant pending, JoinStatus joinStatus) {
        // 상태변경 안됨 : 게시글이 본인것이 아님
        if (!post.getAuthor().getId().equals(authorId)) {
            throw new BaseException(ErrorCode.NO_UPDATE_PERMISSION);
        }

        // 상태변경 안됨 : 신청 대기 상태가 아닐때
        if (pending == null) {
            throw new BaseException(ErrorCode.NOT_PARTICIPATED);
        }

        // 상태변경 안됨 : 대기 상태로 만들려고 할 때
        if (joinStatus.equals(JoinStatus.PENDING)) {
            throw new BaseException(ErrorCode.NOT_CHANGE_PENDING);
        }
    }

    // ===========================================else==========================================================
    // 게시글 인원이 꽉차게 되면 게시글 상태를 마감으로 변경 (CONFIRMED)
    private void changePostStatusForConfirmed(Long postId, Post post) {
        int participantSize = participantRepository.countByPostAndStatus(post, JoinStatus.ACCEPTED);
        if (post.getMaxParticipants().equals(participantSize)) {
            post.updateStatus(PostStatus.CONFIRMED);
            inviteService.deleteAllByPostAndStatus(post, JoinStatus.PENDING);
            // 채팅방 생성
            if (!chattingService.checkPostChatRoom(postId)) {
                chattingService.createFromPost(postId);
            }
            chattingService.registration(post);
        }
    }

    private PostParticipant isFirstProcess(Post post, User user) {
        PostParticipant participant;
        if (post.getIsFirstCome()) {
            int count = participantRepository.countByPostAndStatus(post, JoinStatus.ACCEPTED);

            if (count == post.getMaxParticipants()) {
                throw new BaseException(CANNOT_PARTICIPATE_POST);

            } else {
                participant = PostParticipant.createIsFirst(post, user);

                //참가 이후에 maxParticipants 수를 만족 했을 때
                if (count + 1 == post.getMaxParticipants()) {
                    post.updateStatus(PostStatus.CONFIRMED);
                    inviteService.deleteAllByPostAndStatus(post, JoinStatus.PENDING);
                }
            }
        } else {
            participant = PostParticipant.create(post, user);
        }
        return participant;
    }
    //포스트 작성
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheName.SEARCH_POST, allEntries = true),
            @CacheEvict(value = CacheName.SUGGESTED_POST, allEntries = true),
            @CacheEvict(value = CacheName.WRITTEN_POST, allEntries = true)
    })
    public PostCreateAndUpdateResponseDto createPost(Long userId, PostRequestDto request) {

        Post post = Post.createPost(
                userId,
                request.getTitle(),
                request.getTimeSlot(),
                request.getPublicContent(),
                request.getPrivateContent(),
                request.getMaxParticipants(),
                request.getGenderLimit(),
                request.getJobCategoryLimit(),
                request.getAgeLimit()
        );

        return PostCreateAndUpdateResponseDto.from(post);
    }

    //게시물 조건 검색
    @Transactional(readOnly = true)
    @Cacheable(
            value = CacheName.SEARCH_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #maxParticipants, #ageLimit, #jobCategoryLimit, #genderLimit, #hobbyIdList, #techStackIdList}"
    )
    public PagedResponse<PostResponseWithApplyStatusDto> getPostDtosBySearch(
            Pageable pageable,
            Integer maxParticipants,
            AgeLimit ageLimit,
            JobCategory jobCategoryLimit,
            Gender genderLimit
    ) {

        Page<PostResponseWithApplyStatusDto> postDtosBySearch = postCommandQuery.findPostDtosBySearch(pageable, maxParticipants,
                ageLimit, jobCategoryLimit, genderLimit);
        return PagedResponse.from(postDtosBySearch);
    }
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheName.SEARCH_POST, allEntries = true),
            @CacheEvict(value = CacheName.ONE_POST, key = "#id"),
            @CacheEvict(value = CacheName.LIKED_POST, allEntries = true),
            @CacheEvict(value = CacheName.CONFIRMED_POST, allEntries = true),
            @CacheEvict(value = CacheName.WRITTEN_POST, allEntries = true),
            @CacheEvict(value = CacheName.SUGGESTED_POST, allEntries = true),
    })
    public void deletePostById(Long userId, Long id) {
        Post foundPost = getPostByIdOrElseThrow(id);
        validateAuthor(userId, foundPost);

        foundPost.unlinkReviews();

        //자식 댓글들 먼저 일괄 삭제 외래키 제약 제거
        commentRepository.deleteAllByChildrenPostId(id);
        //postId 기준 댓글 일괄 삭제 메서드 외래키 제약 제거
        commentRepository.deleteAllByPostId(id);
        postRepository.delete(foundPost);
    }
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheName.SEARCH_POST, allEntries = true),
            @CacheEvict(value = CacheName.ONE_POST, key = "#id"),
            @CacheEvict(value = CacheName.LIKED_POST, allEntries = true),
            @CacheEvict(value = CacheName.CONFIRMED_POST, allEntries = true),
            @CacheEvict(value = CacheName.WRITTEN_POST, allEntries = true),
            @CacheEvict(value = CacheName.SUGGESTED_POST, allEntries = true),
    })
    public PostCreateAndUpdateResponseDto updatePostDetails(Long userId, Long id, PostUpdateRequestDto request) {
        Post foundPost = getPostByIdOrElseThrow(id);
        validateAuthor(userId, foundPost);


        foundPost.updatePostIfNotNull(
                request.getTitle(),
                request.getTimeSlot(),
                request.getPublicContent(),
                request.getPrivateContent(),
                request.getMaxParticipants(),
                request.getGenderLimit(),
                request.getJobCategoryLimit(),
                request.getAgeLimit()
        );

        return PostCreateAndUpdateResponseDto.from(foundPost);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheName.SEARCH_POST, allEntries = true),
            @CacheEvict(value = CacheName.ONE_POST, key = "#id"),
            @CacheEvict(value = CacheName.LIKED_POST, allEntries = true),
            @CacheEvict(value = CacheName.CONFIRMED_POST, allEntries = true),
            @CacheEvict(value = CacheName.WRITTEN_POST, allEntries = true),
            @CacheEvict(value = CacheName.SUGGESTED_POST, allEntries = true),
    })
    public PostResponseDto changeStatus(Long userId, Long id, PostStatusRequestDto request) {
        //상태값 변경은 어떤 상태라도 불러와서 수정
        Post foundPost = getPostByIdWithoutStatusLimit(id);
        validateAuthor(userId, foundPost);

        //변동사항 있을시에만 업데이트
        if (!foundPost.getStatus().equals(request.getStatus())) {
            foundPost.updateStatus(request.getStatus());
            //모임 성사로 변경시 채팅방 생성
            if (request.getStatus().equals(PostStatus.CONFIRMED)) {
                // 게시글로 생성된 채팅방이 없는 경우 생성
                if (!chattingService.checkPostChatRoom(foundPost.getId())) {
                    chattingService.createFromPost(foundPost.getId());
                }
                chattingService.registration(foundPost);

            }
        }

        return PostResponseDto.from(foundPost);
    }

    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //

    public Post getPostByIdOrElseThrow(Long id) {
        return postCommandQuery.getPostByIdNotClose(id)
                .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
    }

    public Post getPostByIdWithoutStatusLimit(Long id) {
        return postCommandQuery.findPostById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
    }

    // validate
    void validateAuthor(Long userId, Post post) {
        if (!post.getAuthor().getId().equals(userId)) {
            throw new BaseException(ErrorCode.POST_FORBIDDEN);
        }
    }

    public PostLike getPostLikePostAndUser(Post post, User user) {
        return postLikeRepository.findByPostAndUser(post, user)
                .orElseThrow(() -> new BaseException(ErrorCode.POSTLIKE_NOT_FOUND));
    }

    // validate
    public void validateExists(Post post, User user) {
        if (postLikeRepository.existsByPostAndUser(post, user)) {
            throw new BaseException(ErrorCode.ALREADY_LIKED);
        }
    }

    private Post getPostWithOpen(Long postId) {
        return postRepository.findByIdAndStatus(postId, PostStatus.OPEN)
                .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
    }

    private PostParticipant getParticipantByStatus(User user, Post post, JoinStatus pending) {
        return participantRepository.findByUserAndPost(user, post).stream()
                .filter(p -> p.getStatus().equals(pending))
                .findFirst()
                .orElse(null);
    }


}
