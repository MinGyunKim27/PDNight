package org.example.pdnight.global.init;

import org.example.pdnight.domain.user.domain.userDomain.UserCommandQuery;
import org.example.pdnight.domain.user.domain.userDomain.UserReader;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.global.config.PasswordEncoder;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements ApplicationRunner {
	private final UserReader userReader;
	private final UserCommandQuery userCommandQuery;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		String adminEmail = "admin@pdnight.com";
		String adminName = "관리자";
		String adminPassword = passwordEncoder.encode("password1!");

		//이미 admin 계정이 있을 시
		if (userReader.existsByEmail(adminEmail)) {
			return;
		}

		User admin = User.createAdmin(adminName);
		userCommandQuery.save(admin);
	}

}
