package org.example.pdnight.domain.post.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.pdnight.domain.post.domain.post.PostParticipant;
import org.example.pdnight.domain.post.enums.JoinStatus;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(staticName = "from")
public class ParticipantResponse {

    private Long userId;
    private Long postId;
    private JoinStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private ParticipantResponse(PostParticipant postParticipant) {
        this.userId = postParticipant.getUserId();
        this.postId = postParticipant.getPost().getId();
        this.status = postParticipant.getStatus();
        this.createdAt = postParticipant.getCreatedAt();
        this.updatedAt = postParticipant.getUpdatedAt();
    }

    public static ParticipantResponse toDto(PostParticipant postParticipant) {
        return new ParticipantResponse(postParticipant);
    }
}
