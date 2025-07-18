package org.example.pdnight.domain.participant.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.participant.enums.JoinStatus;
import org.example.pdnight.domain.post.enums.Gender;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(staticName = "of")
public class ParticipantResponse {

    private Long userId;
    private Long postId;
    private String nickname;
    private JobCategory jobCategory;
    private String techStacks;
    private Long age;
    private Gender gender;
    private JoinStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
