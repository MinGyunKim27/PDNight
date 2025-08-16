package org.example.pdnight.domain.post.presentation.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class PostParticipantInfo {

    Long authId;

    List<Long> participants;

    protected PostParticipantInfo(Long authId, List<Long> participants) {
        this.authId = authId;
        this.participants = participants;
    }

    public static PostParticipantInfo create(Long authId, List<Long> participants) {
        return new PostParticipantInfo(authId, participants);
    }
}
