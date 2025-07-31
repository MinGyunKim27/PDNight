package org.example.pdnight.global.filter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.global.common.enums.JobCategory;
import org.example.pdnight.global.common.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final Long userId;
    private final String username;
    private final String password;
    private final UserRole userRole;
    private final Long age;
    private final Gender gender;
    private final JobCategory jobCategory;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userRole));
    }
}
