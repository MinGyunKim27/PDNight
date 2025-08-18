package org.example.pdnight.domain.post.domain.post;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "post_tags")
@Getter
public class PostTagDocument {
    @Id
    private Long id;

    private Long tagId;

    private PostTagDocument(Long id, Long tagId) {
        this.id = id;
        this.tagId = tagId;
    }

    public static PostTagDocument create(Long postId, Long tagId) {
        return new PostTagDocument(postId, tagId);
    }
}
