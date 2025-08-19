package org.example.pdnight.domain.auth.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.global.annotation.ValidEmailPattern;
import org.example.pdnight.global.common.enums.JobCategory;

@Getter
@Builder
public class SignupRequest {
    @Schema(example = "user1@naver.com")
    @NotBlank(message = "이메일은 작성해야 합니다.")
    @ValidEmailPattern
    private String email;

    @Schema(example = "password1!")
    @NotBlank(message = "비밀번호를 작성해야 합니다.")
    private String password;

    @NotBlank(message = "이름을 작성해야 합니다.")
    private String name;

    @NotBlank(message = "닉네임을 작성해야 합니다.")
    private String nickname;

    @NotNull(message = "성별을 작성해야 합니다.")
    private Gender gender;

    @Schema(example = "30")
    @NotNull(message = "나이를 작성해야 합니다.")
    private Long age;

    @NotNull(message = "직업을 작성해야 합니다.")
    private JobCategory jobCategory;
}
