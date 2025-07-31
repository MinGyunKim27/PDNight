package org.example.pdnight.domain.user.presentation.dto.userDto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.example.pdnight.domain.common.enums.JoinStatus;

@Getter
public class ConfirmedPostRequest {
    @NotNull
    private JoinStatus joinStatus;
}
