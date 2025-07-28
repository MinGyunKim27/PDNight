package org.example.pdnight.domain.chatRoom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NicknameResponseDto {
    private final String username;

    public static NicknameResponseDto from(String username) {
        return new NicknameResponseDto(username);
    }
}
