package com.movieing.movieingbackend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 요청 DTO
 *
 * - 로그인 API 호출 시 클라이언트로부터 전달받는 입력 데이터
 * - 이메일/비밀번호에 대한 기본적인 유효성 검증을 수행
 * - 사용자 존재 여부, 비밀번호 일치 여부 등의 비즈니스 검증은 Service 레벨에서 처리
 */
@Getter
@NoArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;    // 로그인에 사용하는 이메일

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password; // 평문 비밀번호 (서버에서 해시 검증)
}
