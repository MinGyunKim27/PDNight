package org.example.pdnight.domain.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.example.pdnight.global.annotation.ValidEmailPattern;

@Getter
@Builder
public class LoginRequest {

    @NotBlank(message = "이메일은 작성해야 합니다.")
    @ValidEmailPattern
    private String email;

    @NotBlank(message = "비밀번호를 작성해야 합니다.")
    private String password;

}
