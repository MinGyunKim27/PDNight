package org.example.pdnight.global.init;

import org.example.pdnight.domain1.user.entity.User;
import org.example.pdnight.domain1.user.repository.UserRepository;
import org.example.pdnight.global.config.PasswordEncoder;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements ApplicationRunner {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		String adminEmail = "admin@pdnight.com";
		String adminName = "관리자";
		String adminPassword = passwordEncoder.encode("password1!");

		//이미 admin 계정이 있을 시
		if (userRepository.existsByEmail(adminEmail)) {
			return;
		}

		User admin = User.createAdmin(adminEmail, adminName, adminPassword);
		userRepository.save(admin);
	}

}