package org.example.pdnight.global.event;

public record CommentReplyCreatedEvent(
        Long commentAuthorId,
        Long replyAuthorId
) {
}
