package org.example.pdnight.domain.auth.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.global.common.enums.UserRole;

import java.time.LocalDateTime;

@Entity
@Table(name = "auth")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private Boolean isDeleted = false;

    private LocalDateTime deletedAt;

    private Auth(String email, String password, UserRole role) {
        this.email = email;
        this.password = password;
        this.role = role == null? UserRole.USER : role;
    }

    public void softDelete(){
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public static Auth create(String email, String password,  UserRole role) {
       return new Auth(email, password, role);
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

}
