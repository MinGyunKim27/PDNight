package org.example.pdnight.domain.chat.presentation.dto.response;

import lombok.Getter;

@Getter
public class NicknameResponse {
    private final String username;

    private NicknameResponse(String username) {
        this.username = username;
    }

    public static NicknameResponse from(String username) {
        return new NicknameResponse(username);
    }
}
