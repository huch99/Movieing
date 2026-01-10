package com.movieing.movieingbackend.auth.dto;

import com.movieing.movieingbackend.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SignupResponseDto {

    private String publicUserId;
    private String userName;
    private String email;
    private UserRole role;
}
