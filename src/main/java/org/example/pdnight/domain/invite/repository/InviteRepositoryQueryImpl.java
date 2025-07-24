package org.example.pdnight.domain.invite.repository;

import org.example.pdnight.domain.invite.dto.response.InviteResponseDto;
import org.example.pdnight.domain.invite.entity.QInvite;
import org.example.pdnight.domain.post.entity.QPost;
import org.example.pdnight.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

@Repository
public class InviteRepositoryQueryImpl implements InviteRepositoryQuery{
    @Override
    public Page<InviteResponseDto> getMyInvited(Long userId) {
        QInvite invite = QInvite.invite;
        QUser user= QUser.user;
        QPost post = QPost.post;


        return null;
    }
}
