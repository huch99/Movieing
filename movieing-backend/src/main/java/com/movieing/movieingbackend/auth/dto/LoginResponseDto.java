package com.movieing.movieingbackend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 로그인 응답 DTO
 *
 * - 로그인 성공 시 클라이언트에 반환되는 인증 응답 모델
 * - JWT Access Token과 함께 사용자 기본 정보를 포함
 * - 클라이언트는 accessToken을 저장하여 이후 인증이 필요한 API 호출 시 사용
 */
@Getter
@AllArgsConstructor
@Builder
public class LoginResponseDto {

    private String accessToken;  // JWT Access Token
    private String tokenType;    // 토큰 타입 (일반적으로 "Bearer")
    private String publicUserId; // 외부 노출용 사용자 식별자(UUID 문자열)
    private String userName;     // 사용자 이름
    private String email;        // 사용자 이메일
    private String role;         // 사용자 권한 ("USER" | "ADMIN" | "THEATER")
}
