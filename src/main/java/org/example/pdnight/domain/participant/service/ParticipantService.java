package org.example.pdnight.domain.participant.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.common.enums.JoinStatus;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.invite.service.InviteService;
import org.example.pdnight.domain.participant.dto.response.ParticipantResponse;
import org.example.pdnight.domain.participant.entity.PostParticipant;
import org.example.pdnight.domain.participant.repository.ParticipantRepository;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.post.repository.PostRepository;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.pdnight.domain.common.enums.ErrorCode.CANNOT_PARTICIPATE_POST;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipantService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final InviteService inviteService;

    // 참가 요건 확인
    private void validForCreateParticipant(User user, Post post) {
        // 신청 안됨 : 본인 게시글에 본인이 신청하는 경우
        if (post.getAuthor().equals(user)) {
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


    //참가 신청
    @Transactional
    public ParticipantResponse applyParticipant(Long loginId, Long postId) {
        User user = getUser(loginId);
        Post post = getPostWithOpen(postId);

        // 신청 안되는지 확인
        validForCreateParticipant(user, post);

        PostParticipant participant;

        //선착순 포스트인 경우
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
        // 정상 신청

        participantRepository.save(participant);

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
    public void deleteParticipant(Long loginId, Long postId) {
        User user = getUser(loginId);
        Post post = getPostWithOpen(postId);

        PostParticipant pending = getParticipantByStatus(user, post, JoinStatus.PENDING);

        // 삭제 안됨 : 신청하지 않거나, 이미 수락 혹은 거절당했을때
        if (pending == null) {
            throw new BaseException(ErrorCode.CANNOT_CANCEL);
        }

        // 정상 삭제
        participantRepository.delete(pending);
    }

    //참가 확정(작성자)
    @Transactional
    public ParticipantResponse changeStatusParticipant(Long authorId, Long userId, Long postId, String status) {
        User user = getUser(userId);
        Post post = getPostWithOpen(postId);
        JoinStatus joinStatus = JoinStatus.of(status);

        PostParticipant pending = getParticipantByStatus(user, post, JoinStatus.PENDING);

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

        // 상태변경
        pending.changeStatus(joinStatus);

        // 게시글 인원이 꽉차게 되면 게시글 상태를 마감으로 변경 (CONFIRMED)
        int participantSize = participantRepository.countByPostAndStatus(post, JoinStatus.ACCEPTED);
        if (post.getMaxParticipants().equals(participantSize)) {
            post.updateStatus(PostStatus.CONFIRMED);
            inviteService.deleteAllByPostAndStatus(post, JoinStatus.PENDING);
        }

        return ParticipantResponse.from(
                userId,
                postId,
                pending.getStatus(),
                pending.getCreatedAt(),
                pending.getUpdatedAt()
        );
    }

    //본인이 작성한 게시글 신청자 목록 조회
    public PagedResponse<ParticipantResponse> getParticipantListByPending(Long authorId, Long postId, int page, int size) {
        Post post = getPost(postId);
        // 신청자 조회 안됨 : 게시글이 본인것이 아님
        if (!post.getAuthor().getId().equals(authorId)) {
            throw new BaseException(ErrorCode.NO_VIEWING_PERMISSION);
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<PostParticipant> postParticipant = participantRepository.findByPostAndStatus(post, JoinStatus.PENDING, pageable);

        return PagedResponse.from(postParticipant.map(p -> ParticipantResponse.from(
                p.getUser().getId(),
                p.getPost().getId(),
                p.getStatus(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        )));
    }

    //수락한 참가자 조회
    public PagedResponse<ParticipantResponse> getParticipantListByAccepted(Long loginId, Long postId, int page, int size) {
        User user = getUser(loginId);
        Post post = getPost(postId);
        // 참가자 조회 안됨 : 게시글 주인이 아니거나, 참가되지 않은 사람들이 조회하는 경우 (미신청, 신청 대기, 거부)
        if (!post.getAuthor().equals(user) && getParticipantByStatus(user, post, JoinStatus.ACCEPTED) == null) {
            throw new BaseException(ErrorCode.NO_VIEWING_PERMISSION);
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<PostParticipant> postParticipant = participantRepository.findByPostAndStatus(post, JoinStatus.ACCEPTED, pageable);

        return PagedResponse.from(postParticipant.map(p -> ParticipantResponse.from(
                p.getUser().getId(),
                p.getPost().getId(),
                p.getStatus(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        )));
    }

    // 이하 헬퍼 메서드

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
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

    public static AgeLimit determineAgeLimit(Long age) {
        if (age >= 20 && age < 30) return AgeLimit.AGE_20S;
        else if (age >= 30 && age < 40) return AgeLimit.AGE_30S;
        else if (age >= 40 && age < 50) return AgeLimit.AGE_40S;
        else if (age >= 50) return AgeLimit.AGE_50S;
        return AgeLimit.ALL;
    }
}
