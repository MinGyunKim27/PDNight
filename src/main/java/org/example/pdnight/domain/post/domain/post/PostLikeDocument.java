package org.example.pdnight.domain.post.domain.post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.global.common.entity.Timestamped;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "post_likes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLikeDocument extends Timestamped {

    private Long postId;

    private Long userId;

    private PostLikeDocument(Long postId, Long userId) {
        this.postId = postId;
        this.userId = userId;
    }

    public static PostLikeDocument create(Long postId, Long userId) {
        return new PostLikeDocument(postId, userId);
    }

}