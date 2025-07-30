package org.example.pdnight.domain.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WithdrawRequestDto {
    @NotBlank(message = "비밀번호를 작성해야 합니다.")
    private String password;
}
