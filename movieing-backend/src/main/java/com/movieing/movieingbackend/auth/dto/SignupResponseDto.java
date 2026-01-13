package com.movieing.movieingbackend.auth.dto;

import com.movieing.movieingbackend.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 회원가입 응답 DTO
 *
 * - 회원가입 성공 시 클라이언트에 반환되는 사용자 기본 정보
 * - 인증 토큰은 포함하지 않으며, 사용자 식별 및 권한 확인 용도로 사용
 * - publicUserId는 외부 노출용 식별자로 이후 인증/조회 API에서 사용됨
 */
@Getter
@AllArgsConstructor
@Builder
public class SignupResponseDto {

    private String publicUserId; // 외부 노출용 사용자 식별자(UUID 문자열)
    private String userName;     // 사용자 이름
    private String email;        // 로그인 및 식별에 사용하는 이메일
    private UserRole role;       // 사용자 권한 (USER / ADMIN / THEATER)
}
