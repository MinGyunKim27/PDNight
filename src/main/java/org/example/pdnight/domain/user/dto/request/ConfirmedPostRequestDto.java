package org.example.pdnight.domain.user.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.example.pdnight.domain.common.enums.JoinStatus;

@Getter
public class ConfirmedPostRequestDto {
    @NotNull
    private JoinStatus joinStatus;
}
