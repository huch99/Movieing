package com.movieing.movieingbackend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원가입 요청 DTO
 *
 * - 회원가입 API 호출 시 클라이언트로부터 전달받는 입력 데이터
 * - Validation 어노테이션을 통해 기본적인 입력값 검증 수행
 * - 비즈니스 규칙(중복, 상태 등)은 Service 레벨에서 처리
 */
@Getter
@NoArgsConstructor
public class SignupRequestDto {

    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 100, message = "이름은 최대 100자까지 가능합니다.")
    private String userName; // 사용자 이름

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 255, message = "이메일은 최대 255자까지 가능합니다.")
    private String email; // 로그인 및 사용자 식별용 이메일

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 50, message = "비밀번호는 8~50자여야 합니다.")
    private String password; // 평문 비밀번호 (서버에서 해시 처리)

    @NotBlank(message = "전화번호는 필수입니다.")
    @Size(max = 20, message = "전화번호는 최대 20자까지 가능합니다.")
    private String phone; // 사용자 휴대폰 번호
}
