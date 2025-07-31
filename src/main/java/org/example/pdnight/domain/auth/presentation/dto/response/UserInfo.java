package org.example.pdnight.domain.auth.presentation.dto.response;

import lombok.Getter;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.post.enums.Gender;

@Getter
public class UserInfo {
    private String nickname;
    private Long age;
    private Gender gender;
    private JobCategory jobCategory;

    private UserInfo(String nickname,Long age, Gender gender, JobCategory jobCategory){
        this.nickname = nickname;
        this.age = age;
        this.gender = gender;
        this.jobCategory = jobCategory;
    }

    public static UserInfo from(String nickname,Long age, Gender gender, JobCategory jobCategory){
        return new UserInfo(nickname,age,gender,jobCategory);
    }
}
