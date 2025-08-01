package org.example.pdnight.global.init;

import org.example.pdnight.domain.auth.domain.AuthCommander;
import org.example.pdnight.domain.auth.domain.AuthReader;
import org.example.pdnight.domain.auth.domain.entity.Auth;
import org.example.pdnight.domain.auth.presentation.dto.request.SignupRequest;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.user.domain.userDomain.UserCommander;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.global.common.enums.JobCategory;
import org.example.pdnight.global.common.enums.UserRole;
import org.example.pdnight.global.config.PasswordEncoder;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements ApplicationRunner {

    private final AuthReader authReader;
    private final UserCommander userCommander;
    private final AuthCommander authCommander;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        String adminEmail = "admin@pdnight.com";
        String adminName = "관리자";
        String adminPassword = passwordEncoder.encode("password1!");

        //이미 admin 계정이 있을 시
        if (authReader.findByEmail(adminEmail).isPresent()) {
            return;
        }

        SignupRequest request = SignupRequest.builder()
                .email(adminEmail).name(adminName).password(adminPassword)
                .gender(Gender.MALE).age(25L).jobCategory(JobCategory.BACK_END_DEVELOPER)
                .build();

        Auth auth = Auth.create(
                request.getEmail(),
                adminPassword,
                UserRole.ADMIN
        );
        authCommander.save(auth);

        User admin = User.fromUserSignUpEvent(request);
        userCommander.save(admin);
    }

}
