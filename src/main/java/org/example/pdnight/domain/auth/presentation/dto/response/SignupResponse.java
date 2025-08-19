package org.example.pdnight.domain.auth.presentation.dto.response;

import lombok.Getter;
import org.example.pdnight.domain.auth.domain.entity.Auth;

@Getter
public class SignupResponse {
    private String email;

    private SignupResponse(String email) {
        this.email = email;
    }

    public static SignupResponse from(Auth auth) {
        return new SignupResponse(auth.getEmail());
    }
}
