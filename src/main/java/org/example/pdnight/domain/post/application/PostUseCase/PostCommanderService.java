package org.example.pdnight.domain.post.application.PostUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.domain.post.PostCommander;
import org.example.pdnight.domain.post.domain.post.PostLike;
import org.example.pdnight.domain.post.domain.post.PostParticipant;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.post.presentation.dto.request.PostRequestDto;
import org.example.pdnight.domain.post.presentation.dto.request.PostStatusRequestDto;
import org.example.pdnight.domain.post.presentation.dto.request.PostUpdateRequestDto;
import org.example.pdnight.domain.post.presentation.dto.response.ParticipantResponse;
import org.example.pdnight.domain.post.presentation.dto.response.PostLikeResponse;
import org.example.pdnight.domain.post.presentation.dto.response.PostResponseDto;
import org.example.pdnight.global.aop.DistributedLock;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.enums.JobCategory;
import org.example.pdnight.global.common.exception.BaseException;
import org.example.pdnight.global.constant.CacheName;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.example.pdnight.global.common.enums.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class PostCommanderService {

    private final PostCommander postCommander;

    // region  게시물 신청자
    //참가 신청
    @Transactional
    @CacheEvict(value = CacheName.CONFIRMED_POST, allEntries = true)
    @DistributedLock(key = "#postId", timeoutMs = 5000)
    public ParticipantResponse applyParticipant(Long loginId, Long age, Gender gender, JobCategory jobCategory, Long postId) {
        Post foundPost = getPostWithOpen(postId);

        // 신청 안되는지 확인
        validForCreateParticipant(loginId, age, gender, jobCategory, foundPost);

        //선착순 포스트인 경우
        PostParticipant participant = handleJoinRequest(foundPost, loginId);

        // 정상 신청
        foundPost.addParticipants(participant);

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
    @DistributedLock(key = "#postId", timeoutMs = 5000)
    public void deleteParticipant(Long loginId, Long postId) {
        Post post = getPostWithOpen(postId);

        PostParticipant pending = getParticipantByStatus(loginId, post, JoinStatus.PENDING);

        // 삭제 안됨 : 신청하지 않거나, 이미 수락 혹은 거절당했을때
        if (pending == null) {
            throw new BaseException(ErrorCode.CANNOT_CANCEL);
        }

        // 정상 삭제
        post.removeParticipant(pending);
    }

    //참가 확정(작성자)
    @Transactional
    @CacheEvict(value = CacheName.CONFIRMED_POST, allEntries = true)
    @DistributedLock(key = "#postId", timeoutMs = 5000)
    public ParticipantResponse changeStatusParticipant(Long authorId, Long userId, Long postId, String status) {
        Post post = getPostWithOpen(postId);
        JoinStatus joinStatus = JoinStatus.of(status);

        PostParticipant pending = getParticipantByStatus(userId, post, JoinStatus.PENDING);

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
    //endregion

    //region 게시물 좋아요
    @CacheEvict(value = CacheName.LIKED_POST, allEntries = true)
    @Transactional
    public PostLikeResponse addLike(Long id, Long userId) {

        Post post = getPostByIdOrElseThrow(id);

        //좋아요 존재 하면 에러
        validateExists(post, userId);

        PostLike postLike = PostLike.create(post, userId);
        post.addLike(postLike);

        return PostLikeResponse.from(postLike);
    }

    @CacheEvict(value = CacheName.LIKED_POST, allEntries = true)
    @Transactional
    public void removeLike(Long id, Long userId) {

        Post post = getPostByIdOrElseThrow(id);
        PostLike like = getPostLikePostAndUser(post, userId);

        post.removeLike(like);
    }

    //endregion

    //region 게시글
    //포스트 작성
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheName.SEARCH_POST, allEntries = true),
            @CacheEvict(value = CacheName.SUGGESTED_POST, allEntries = true),
            @CacheEvict(value = CacheName.WRITTEN_POST, allEntries = true)
    })
    public PostResponseDto createPost(Long userId, PostRequestDto request) {

        Post post = Post.createPost(
                userId,
                request.getTitle(),
                request.getTimeSlot(),
                request.getPublicContent(),
                request.getMaxParticipants(),
                request.getGenderLimit(),
                request.getJobCategoryLimit(),
                request.getAgeLimit()
        );

        return PostResponseDto.toDto(post);
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

        //자식 댓글들 먼저 일괄 삭제 외래키 제약 제거 todo: 추후 이벤트 처리를 통해서 게시글 댓글 삭제 초대 삭제등
        // commentRepository.deleteAllByChildrenPostId(id);
        //postId 기준 댓글 일괄 삭제 메서드 외래키 제약 제거
        // commentRepository.deleteAllByPostId(id);
        postCommander.deletePost(foundPost);
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
    public PostResponseDto updatePostDetails(Long userId, Long id, PostUpdateRequestDto request) {
        Post foundPost = getPostByIdOrElseThrow(id);
        validateAuthor(userId, foundPost);

        foundPost.updatePostIfNotNull(
                request.getTitle(),
                request.getTimeSlot(),
                request.getPublicContent(),
                request.getMaxParticipants(),
                request.getGenderLimit(),
                request.getJobCategoryLimit(),
                request.getAgeLimit()
        );

        return PostResponseDto.toDto(foundPost);
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
    public PostResponseDto changePostStatus(Long userId, Long id, PostStatusRequestDto request) {
        //상태값 변경은 어떤 상태라도 불러와서 수정
        Post foundPost = getPostByIdWithoutStatusLimit(id);
        validateAuthor(userId, foundPost);

        //변동사항 있을시에만 업데이트
        if (!foundPost.getStatus().equals(request.getStatus())) {
            foundPost.updateStatus(request.getStatus());
//            //모임 성사로 변경시 채팅방 생성      -> 이벤트 처리
//            if (request.getStatus().equals(PostStatus.CONFIRMED)) {
//                // 게시글로 생성된 채팅방이 없는 경우 생성
//                if (!chattingService.checkPostChatRoom(foundPost.getId())) {
//                    chattingService.createFromPost(foundPost.getId());
//                }
//                chattingService.registration(foundPost);
//
//            }
        }

        return PostResponseDto.toDto(foundPost);
    }

    public void deleteAdminPostById(Long id) {
        postCommander.deletePost(getPostByIdOrElseThrow(id));
    }
    //endregion

    //region ----------------------------------- HELPER 메서드 ------------------------------------------------------

    private PostLike getPostLikePostAndUser(Post post, Long userId) {
        return post.getPostLikes().stream()
                .filter(postLike -> postLike.getUserId().equals(userId)).findFirst()
                .orElseThrow(() -> new BaseException(POSTLIKE_NOT_FOUND));
    }

    // validate
    private void validateExists(Post post, Long userId) {
        if (post.getPostLikes().stream().noneMatch(postLike -> postLike.getUserId().equals(userId))) {
            throw new BaseException(ErrorCode.ALREADY_LIKED);
        }
    }

    private Post getPostByIdOrElseThrow(Long postId) {
        return postCommander.findPostById(postId)
                .orElseThrow(() -> new BaseException(POST_NOT_FOUND));
    }

    private Post getPostWithOpen(Long postId) {
        return postCommander.findByIdAndStatus(postId, PostStatus.OPEN)
                .orElseThrow(() -> new BaseException(POST_NOT_FOUND));
    }

    private Post getPostByIdWithoutStatusLimit(Long id) {
        return postCommander.findPostById(id)
                .orElseThrow(() -> new BaseException(POST_NOT_FOUND));
    }

    //참가자 생성, 추가 메서드
    private PostParticipant handleJoinRequest(Post post, Long userId) {

        int count = acceptedParticipantsCounter(post.getPostParticipants());

        if (count == post.getMaxParticipants()) {
            throw new BaseException(CANNOT_PARTICIPATE_POST);
        }

        PostParticipant participant = PostParticipant.create(post, userId);

        if (post.getIsFirstCome()) {
            participant.changeStatus(JoinStatus.ACCEPTED);

            if (count + 1 == post.getMaxParticipants()) {
                post.updateStatus(PostStatus.CONFIRMED);
            }
        }

        post.addParticipants(participant);

        return participant;
    }

    // 리스트를 불러와서 참여자 수
    private int acceptedParticipantsCounter(List<PostParticipant> participants) {
        return (int) participants.stream()
                .filter(participant -> participant.getStatus() == JoinStatus.ACCEPTED)
                .count();
    }

    private static AgeLimit determineAgeLimit(Long age) {
        if (age >= 20 && age < 30) return AgeLimit.AGE_20S;
        else if (age >= 30 && age < 40) return AgeLimit.AGE_30S;
        else if (age >= 40 && age < 50) return AgeLimit.AGE_40S;
        else if (age >= 50) return AgeLimit.AGE_50S;
        return AgeLimit.ALL;
    }

    // 참가 요건 확인 (신청 할 때 validate)
    private void validForCreateParticipant(Long userId, Long age, Gender gender, JobCategory jobCategory, Post post) {

        // 신청 안됨 : 본인 게시글에 본인이 신청하는 경우
        if (post.getAuthorId().equals(userId)) {
            throw new BaseException(ErrorCode.CANNOT_PARTICIPATE_SELF);
        }

        //나이 조건 안 맞으면 신청 불가
        AgeLimit userAgeLimit = determineAgeLimit(age);
        if (post.getAgeLimit() != AgeLimit.ALL && post.getAgeLimit() != userAgeLimit) {
            throw new BaseException(ErrorCode.AGE_LIMIT_NOT_SATISFIED);
        }

        //성별 조건 안 맞으면 신청 불가
        if (post.getGenderLimit() != Gender.ALL && post.getGenderLimit() != gender) {
            throw new BaseException(ErrorCode.GENDER_LIMIT_NOT_SATISFIED);
        }

        //직업군 조건 안 맞으면 신청 불가
        if (post.getJobCategoryLimit() != JobCategory.ALL && post.getJobCategoryLimit() != jobCategory) {
            throw new BaseException(ErrorCode.JOB_CATEGORY_LIMIT_NOT_SATISFIED);
        }

        // 신청 안됨 : 이미 신청함 - JoinStatus status 가 대기중 or 수락됨 인 경우
        PostParticipant pending = getParticipantByStatus(userId, post, JoinStatus.PENDING);
        PostParticipant accepted = getParticipantByStatus(userId, post, JoinStatus.ACCEPTED);

        if (pending != null) {
            throw new BaseException(ErrorCode.POST_ALREADY_PENDING);
        }
        if (accepted != null) {
            throw new BaseException(ErrorCode.POST_ALREADY_ACCEPTED);
        }
    }

    private PostParticipant getParticipantByStatus(Long userId, Post post, JoinStatus joinStatus) {
        List<PostParticipant> postParticipants = post.getPostParticipants();
        return postParticipants.stream()
                .filter(PostParticipant -> PostParticipant.getUserId().equals(userId) && PostParticipant.getStatus().equals(joinStatus))
                .findFirst()
                .orElse(null);
    }

    // 게시글 인원이 꽉차게 되면 게시글 상태를 마감으로 변경 (CONFIRMED)
    private void changePostStatusForConfirmed(Long postId, Post post) {
        int participantSize = acceptedParticipantsCounter(post.getPostParticipants());
        if (post.getMaxParticipants().equals(participantSize)) {
            post.updateStatus(PostStatus.CONFIRMED);
            // 이벤트 발생
//            inviteService.deleteAllByPostAndStatus(post, JoinStatus.PENDING);
//            // 채팅방 생성
//            if (!chattingService.checkPostChatRoom(postId)) {
//                chattingService.createFromPost(postId);
//            }
//            chattingService.registration(post);
        }
    }

    // validate - 작성자가 맞는지 검증
    private void validateAuthor(Long userId, Post post) {
        if (!post.getAuthorId().equals(userId)) {
            throw new BaseException(POST_FORBIDDEN);
        }
    }

    // 상태 변경 가능 확인 (상태가 변경될 때 validate)
    private void validForChangeStatusParticipant(Long authorId, Post post, PostParticipant pending, JoinStatus joinStatus) {
        // 상태변경 안됨 : 게시글이 본인것이 아님
        if (!post.getAuthorId().equals(authorId)) {
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


    //endregion
}