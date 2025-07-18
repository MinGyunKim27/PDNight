package org.example.pdnight.domain.postLike.dto.response;

import org.example.pdnight.domain.postLike.entity.PostLike;

import java.time.LocalDateTime;

public class PostLikeResponse {

   private Long postId;
   private Long userId;
   private LocalDateTime createdAt;
   private LocalDateTime updatedAt;

   public PostLikeResponse(PostLike postLike) {
       this.postId = postLike.getId();
       this.userId = postLike.getUser().getId();
       this.createdAt = postLike.getCreatedAt();
       this.updatedAt = postLike.getModifiedAt();
   }
}
