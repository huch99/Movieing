package com.movieing.movieingbackend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 내 정보 조회 응답 DTO
 *
 * - 인증된 사용자가 자신의 정보를 조회할 때(/auth/me) 반환되는 응답 모델
 * - JWT 토큰에 포함된 사용자 정보를 기반으로 조회 결과를 전달
 * - 인증/권한 분기(UI 처리)에 필요한 최소한의 정보만 포함
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeResponseDto {

    private String publicUserId; // 외부 노출용 사용자 식별자(UUID 문자열)
    private String userName;     // 사용자 이름
    private String email;        // 사용자 이메일
    private String role;         // 사용자 권한 ("USER" | "ADMIN" | "THEATER")

}
