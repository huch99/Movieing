package com.movieing.movieingbackend.screen.dto.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 상영관 완료 처리 요청 DTO (관리자)
 *
 * - 상영관 초안(DRAFT)을 운영 가능한 상태로 전환할 때 사용
 * - 필수 정보 검증(@NotBlank, @NotNull)을 수행
 * - 검증 통과 후 서비스 레이어에서 상태를 ACTIVE로 전환
 *
 * 검증 책임:
 * - 필수 입력 여부: DTO (@NotBlank, @NotNull)
 * - 정책성 검증(극장 존재/중복 이름 등): 서비스 레이어
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreenCompleteAdminRequestDto {
    @NotNull(message = "영화관 ID는 필수입니다.")
    private Long theaterId;     // 소속 영화관 ID (필수)

    @NotBlank(message = "상영관 이름은 필수입니다.")
    private String screenName;  // 상영관 이름 (필수)

    @NotNull(message = "수용 인원은 필수입니다.")
    @Min(value = 0, message = "수용 인원은 0 이상이어야 합니다.")
    private Integer capacity;   // 수용 인원 (필수, 0 이상)
}
