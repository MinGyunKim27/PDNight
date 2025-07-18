package org.example.pdnight.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
}
