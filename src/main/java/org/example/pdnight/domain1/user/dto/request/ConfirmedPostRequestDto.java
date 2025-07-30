package org.example.pdnight.domain1.user.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.example.pdnight.domain1.common.enums.JoinStatus;

@Getter
public class ConfirmedPostRequestDto {
    @NotNull
    private JoinStatus joinStatus;
}
