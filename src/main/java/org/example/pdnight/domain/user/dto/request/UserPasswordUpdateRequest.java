package org.example.pdnight.domain.user.dto.request;

import lombok.Getter;

@Getter
public class UserPasswordUpdateRequest {
    private String oldPassword ;
    private String newPassword ;
}
