package org.example.pdnight.domain1.user.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserPasswordUpdateRequest {
    @NotNull
    private String oldPassword;
    @NotNull
    private String newPassword;
}
