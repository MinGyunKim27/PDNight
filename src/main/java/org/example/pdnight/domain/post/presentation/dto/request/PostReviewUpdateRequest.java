package org.example.pdnight.domain.post.presentation.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter //form-data로 매핑하기 위해서 사용
public class PostReviewUpdateRequest {
    private String title;
    private String comment;
    private List<MultipartFile> images;
    private List<MultipartFile> videos;
}
