package org.example.pdnight.domain.auth.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WithdrawRequest {

    @Schema(example = "password1!")
    @NotBlank(message = "비밀번호를 작성해야 합니다.")
    private String password;

}
