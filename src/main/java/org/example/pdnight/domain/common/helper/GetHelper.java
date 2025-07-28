package org.example.pdnight.domain.common.helper;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.repository.PostRepositoryQuery;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetHelper {

    private final UserRepository userRepository;
    private final PostRepositoryQuery postRepositoryQuery;
//    private final PostRepository postRepository;
//    private final CommentRepository commentRepository;
//    private final CouponRepository couponRepository;
//    private final EventRepository eventRepository;

    public User getUserById(Long id) {
        return userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
    }

    public Post getPostById(Long id) {
        return postRepository.findByIdAndStatus(id, PostStatus.OPEN)
                .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
    }

//    public Comment getCommentById(Long id) {
//        return commentRepository.findById(id)
//                .orElseThrow(() -> new BaseException(ErrorCode.COMMENT_NOT_FOUND));
//    }
//
//    public Coupon getCouponById(Long id) {
//        return couponRepository.findById(id)
//                .orElseThrow(() -> new BaseException(ErrorCode.COUPON_NOT_FOUND));
//    }
//
//    public Event getEventById(Long id) {
//        return eventRepository.findById(id).orElseThrow(
//                () -> new BaseException(ErrorCode.EVENT_NOT_FOUNT)
//        );
//    }
}
