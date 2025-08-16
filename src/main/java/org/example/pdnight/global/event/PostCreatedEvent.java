package org.example.pdnight.global.event;

import org.example.pdnight.domain.post.domain.post.InviteDocument;
import org.example.pdnight.domain.post.domain.post.PostLikeDocument;
import org.example.pdnight.domain.post.domain.post.PostParticipantDocument;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.global.common.enums.JobCategory;

import java.time.LocalDateTime;
import java.util.List;

public record PostCreatedEvent (
        Long id,
        Long authorId,
        String title,
        LocalDateTime timeSlot,
        String publicContent,
        PostStatus status,
        Integer maxParticipants,
        Gender genderLimit,
        JobCategory jobCategoryLimit,
        AgeLimit ageLimit,
        Boolean isFirstCome,
        List<PostLikeDocument> postLikes,
        List<PostParticipantDocument> postParticipants,
        List<InviteDocument> invites,
        List<String> tags,
        Boolean isDeleted,
        LocalDateTime deletedAt,
        LocalDateTime createdAt
){
}
