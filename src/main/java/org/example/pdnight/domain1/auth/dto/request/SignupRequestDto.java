package org.example.pdnight.domain1.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.example.pdnight.domain1.common.enums.JobCategory;
import org.example.pdnight.domain1.post.enums.Gender;
import org.example.pdnight.domain1.user.enums.Region;
import org.example.pdnight.global.annotation.ValidEmailPattern;

import java.util.List;

@Getter
@Builder
public class SignupRequestDto {
    @NotBlank(message = "이메일은 작성해야 합니다.")
    @ValidEmailPattern
    private String email;

    @NotBlank(message = "비밀번호를 작성해야 합니다.")
    private String password;

    @NotBlank(message = "이름을 작성해야 합니다.")
    private String name;

    private String nickname;

//    @NotBlank(message = "취미를 작성해야 합니다.")
    private List<Long> hobbyIdList;

//    @NotBlank(message = "기술 스택을 작성해야 합니다.")
    private List<Long> techStackIdList;

    @NotNull(message = "성별을 작성해야 합니다.")
    private Gender gender;

    @NotNull(message = "나이를 작성해야 합니다.")
    private Long age;

    @NotNull(message = "직업을 작성해야 합니다.")
    private JobCategory jobCategory;

    @NotNull(message = "사는 지역을 작성해야 합니다.")
    private Region region;

    @NotNull(message = "근무 지역을 작성해야 합니다.")
    private Region workLocation;

    private String comment;

}
