package org.example.pdnight.domain.participant.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.participant.dto.response.ParticipantResponse;
import org.example.pdnight.domain.participant.entity.PostParticipant;
import org.example.pdnight.domain.participant.enums.JoinStatus;
import org.example.pdnight.domain.participant.repository.ParticipantRepository;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.post.repository.PostRepository;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipantService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND.getStatus(), ErrorCode.USER_NOT_FOUND.getMessage()));
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다"));
    }

    private PostParticipant getParticipantByStatus(User user, Post post, JoinStatus pending) {
        return participantRepository.findByUserAndPost(user, post).stream()
                .filter(p -> p.getStatus().equals(pending))
                .findFirst()
                .orElse(null);
    }

    private void validForCreateParticipant(User user, Post post) {
        // 신청 안됨 : 열려있는 게시글만 신청 가능 - PostStatus status 가 open 가 아닌경우
        if (post.getStatus() != PostStatus.OPEN) {
            throw new BaseException(HttpStatus.CONFLICT, "신청할 수 없습니다.");
        }

        // 신청 안됨 : 이미 신청함 - JoinStatus status 가 대기중 or 수락됨 인 경우
        PostParticipant pending = getParticipantByStatus(user, post, JoinStatus.PENDING);
        PostParticipant accepted = getParticipantByStatus(user, post, JoinStatus.ACCEPTED);

        if (pending != null) {
            throw new BaseException(HttpStatus.CONFLICT, "이미 신청했습니다.");
        }
        if (accepted != null) {
            throw new BaseException(HttpStatus.CONFLICT, "이미 가입되어있습니다.");
        }
    }

    @Transactional
    public ParticipantResponse applyParticipant(Long userId, Long postId) {
        User user = getUser(userId);
        Post post = getPost(postId);

        // 신청 안되는지 확인
        validForCreateParticipant(user, post);

        // 정상 신청
        PostParticipant participant = PostParticipant.create(post, user);
        participantRepository.save(participant);

        return ParticipantResponse.of(
                userId,
                postId,
                participant.getStatus(),
                participant.getCreatedAt(),
                participant.getUpdatedAt()
        );
    }

    @Transactional
    public void deleteParticipant(Long userId, Long postId) {
        User user = getUser(userId);
        Post post = getPost(postId);

        // 삭제 안됨 : 신청하지 않거나, 이미 수락 혹은 거절당했을때
        PostParticipant pending = getParticipantByStatus(user, post, JoinStatus.PENDING);

        if (pending == null) {
            throw new BaseException(HttpStatus.CONFLICT, "취소할 수 없습니다.");
        }

        // 정상 삭제
        participantRepository.delete(pending);
    }

    @Transactional
    public ParticipantResponse changeStatusParticipant(Long userId, Long postId, String status) {
        User user = getUser(userId);
        Post post = getPost(postId);
        JoinStatus joinStatus = JoinStatus.of(status);

        PostParticipant pending = getParticipantByStatus(user, post, JoinStatus.PENDING);

        // 상태변경 안됨 : 신청 대기 상태가 아닐때
        if (pending == null || joinStatus.equals(JoinStatus.PENDING)) {
            throw new BaseException(HttpStatus.CONFLICT, "수락 혹은 거절할 수 없습니다.");
        }

        // 상태변경
        pending.changeStatus(joinStatus);

        return ParticipantResponse.of(
                userId,
                postId,
                pending.getStatus(),
                pending.getCreatedAt(),
                pending.getUpdatedAt()
        );
    }

    public PagedResponse<ParticipantResponse> getParticipantListByStatus(Long postId, JoinStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Post post = getPost(postId);
        Page<PostParticipant> postParticipant = participantRepository.findByPostAndStatus(post, status, pageable);

        return PagedResponse.from(postParticipant.map(p -> ParticipantResponse.of(
                p.getUser().getId(),
                p.getPost().getId(),
                p.getStatus(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        )));
    }
}
