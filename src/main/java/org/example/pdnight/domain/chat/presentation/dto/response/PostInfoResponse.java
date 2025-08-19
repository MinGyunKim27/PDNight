package org.example.pdnight.domain.chat.presentation.dto.response;

import lombok.Getter;
import org.example.pdnight.domain.post.domain.post.PostParticipant;

import java.util.List;

@Getter
public class PostInfoResponse {
    private Long id;
    private Long authorId;
    private String title;
    private List<PostParticipant> postParticipants;

    private PostInfoResponse(Long id, Long authorId, String title, List<PostParticipant> postParticipants) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.postParticipants = postParticipants;
    }

    public static PostInfoResponse create(Long id, Long authorId, String title, List<PostParticipant> postParticipants) {
        return new PostInfoResponse(id, authorId, title, postParticipants);
    }
}
