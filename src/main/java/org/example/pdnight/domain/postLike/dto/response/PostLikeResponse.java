package org.example.pdnight.domain.postLike.dto.response;

import lombok.Getter;
import org.example.pdnight.domain.postLike.entity.PostLike;

import java.time.LocalDateTime;

@Getter
public class PostLikeResponse {

   private Long postId;
   private Long userId;
   private LocalDateTime createdAt;
   private LocalDateTime updatedAt;

   public PostLikeResponse(PostLike postLike) {
       this.postId = postLike.getPost().getId();
       this.userId = postLike.getUser().getId();
       this.createdAt = postLike.getCreatedAt();
       this.updatedAt = postLike.getUpdatedAt();
   }
}
