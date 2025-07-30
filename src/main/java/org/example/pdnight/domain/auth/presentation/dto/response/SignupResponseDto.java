package org.example.pdnight.domain.auth.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.pdnight.domain.auth.domain.entity.Auth;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.common.enums.UserRole;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.enums.Region;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class SignupResponseDto {
    private String email;

    private SignupResponseDto(String email) {
        this.email = email;
    }

    public static SignupResponseDto from(Auth auth) {
        return new SignupResponseDto(auth.getEmail());
    }
}
