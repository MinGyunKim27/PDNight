package org.example.pdnight.domain.auth.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserPasswordUpdateRequest {
    @Schema(example = "password1!")
    @NotNull
    private String oldPassword;

    @Schema(example = "password2!")
    @NotNull
    private String newPassword;
}
