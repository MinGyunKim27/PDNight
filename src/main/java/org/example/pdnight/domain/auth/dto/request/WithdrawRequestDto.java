package org.example.pdnight.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class WithdrawRequestDto {
    @NotBlank
    private String password;
}
