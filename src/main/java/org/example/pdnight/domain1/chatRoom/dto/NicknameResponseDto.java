package org.example.pdnight.domain1.chatRoom.dto;

import lombok.Getter;

@Getter
public class NicknameResponseDto {
    private final String username;

    private NicknameResponseDto(String username) {
        this.username = username;
    }

    public static NicknameResponseDto from(String username) {
        return new NicknameResponseDto(username);
    }
}
