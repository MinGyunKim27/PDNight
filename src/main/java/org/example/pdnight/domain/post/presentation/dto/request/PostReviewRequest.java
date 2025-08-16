package org.example.pdnight.domain.post.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter //form-data로 매핑하기 위해서 사용
public class PostReviewRequest {

    @NotBlank(message = "게시글의 id는 입력되어야 합니다.")
    private String postId;
    @NotBlank(message = "후기 제목은 입력되어야 합니다.")
    private String title;
    @NotBlank(message = "후기 내용은 입력되어야 합니다.")
    private String comment;
    private List<MultipartFile> images;
    private List<MultipartFile> videos;

}
