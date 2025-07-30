package org.example.pdnight.domain.common.helper;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.repository.PostRepositoryQuery;
import org.example.pdnight.domain.user.domain.userDomain.UserReader;
import org.example.pdnight.domain.user.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetHelper {

    private final UserReader userReader;
    private final PostRepositoryQuery postRepositoryQuery;

    public User getUserByIdOrElseThrow(Long id) {
        return userReader.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
    }

    public Post getPostByIdOrElseThrow(Long id) {
        return postRepositoryQuery.getPostByIdNotClose(id)
                .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
    }

}
