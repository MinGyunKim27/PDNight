package org.example.pdnight.domain.post.presentation.dto.request;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
public class PostReviewUpdateRequest {
    private String title;
    private String comment;
    private List<MultipartFile> images;
    private List<MultipartFile> videos;
}
