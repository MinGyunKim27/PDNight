package org.example.pdnight.domain.participant.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.pdnight.domain.common.enums.JoinStatus;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(staticName = "of")
public class ParticipantResponse {

    private Long userId;
    private Long postId;
    private JoinStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
