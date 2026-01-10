package com.movieing.movieingbackend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LoginResponseDto {

    private String accessToken;     // JWT
    private String tokenType;       // Bearer
    private String publicUserId;    // 외부 노출용 UUID
    private String userName;
    private String email;
    private String role;
}
