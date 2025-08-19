package org.example.pdnight.domain.auth.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.example.pdnight.global.annotation.ValidEmailPattern;

@Getter
@Builder
public class LoginRequest {

    @Schema(example = "user1@naver.com")
    @NotBlank(message = "이메일은 작성해야 합니다.")
    @ValidEmailPattern
    private String email;

    @Schema(example = "password1!")
    @NotBlank(message = "비밀번호를 작성해야 합니다.")
    private String password;

}
