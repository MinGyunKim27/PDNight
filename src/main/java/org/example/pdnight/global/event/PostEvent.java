package org.example.pdnight.global.event;

import org.example.pdnight.domain.post.domain.post.PostDocument;

public record PostEvent(
        Operation operation,
        PostDocument document
) {
    public enum Operation {
        CREATE, UPDATE, DELETE
    }
}
