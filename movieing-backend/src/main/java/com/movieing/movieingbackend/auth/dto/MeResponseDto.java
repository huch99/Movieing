package com.movieing.movieingbackend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeResponseDto {

    private String publicUserId;
    private String userName;
    private String email;
    private String role; // "USER" | "ADMIN" | "THEATER"

}
