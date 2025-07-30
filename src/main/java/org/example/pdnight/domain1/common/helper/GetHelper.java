package org.example.pdnight.domain1.common.helper;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain1.common.enums.ErrorCode;
import org.example.pdnight.domain1.common.exception.BaseException;
import org.example.pdnight.domain1.post.entity.Post;
import org.example.pdnight.domain1.post.repository.PostRepositoryQuery;
import org.example.pdnight.domain1.user.entity.User;
import org.example.pdnight.domain1.user.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetHelper {

    private final UserRepository userRepository;
    private final PostRepositoryQuery postRepositoryQuery;

    public User getUserByIdOrElseThrow(Long id) {
        return userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
    }

    public Post getPostByIdOrElseThrow(Long id) {
        return postRepositoryQuery.getPostByIdNotClose(id)
                .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
    }

}
