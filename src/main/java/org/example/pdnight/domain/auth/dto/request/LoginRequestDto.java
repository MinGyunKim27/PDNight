package org.example.pdnight.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.example.pdnight.global.annotation.ValidEmailPattern;

@Getter
@Builder
public class LoginRequestDto {
    @NotBlank
    @ValidEmailPattern
    private String email;
    @NotBlank
    private String password;
}
