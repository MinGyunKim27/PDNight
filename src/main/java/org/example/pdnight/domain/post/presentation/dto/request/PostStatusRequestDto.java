package org.example.pdnight.domain.post.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.pdnight.domain.post.enums.PostStatus;

@Getter
@AllArgsConstructor
public class PostStatusRequestDto {

    @NotBlank(message = "변경할 상태값은 필수입력값입니다.")
    private PostStatus status;

}
