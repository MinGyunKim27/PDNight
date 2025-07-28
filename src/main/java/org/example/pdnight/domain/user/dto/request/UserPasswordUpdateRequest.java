package org.example.pdnight.domain.user.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserPasswordUpdateRequest {
    @NotNull
    private String oldPassword;
    @NotNull
    private String newPassword;
}
