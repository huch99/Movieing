package com.movieing.movieingbackend.theater.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * 영화관 완료 처리 요청 DTO (관리자)
 * <p>
 * - 영화관 초안(DRAFT)을 운영 가능한 상태로 전환할 때 사용하는 요청 DTO
 * - 필수 정보에 대한 검증(@NotBlank, @NotNull)을 수행
 * - 검증 통과 후 서비스 레이어에서 상태를 ACTIVE로 전환
 * <p>
 * 검증 책임:
 * - 필수 입력 여부: DTO (@NotBlank, @NotNull)
 * - 정책성 검증(위경도 쌍 입력 등): 서비스 레이어
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TheaterCompleteAdminRequestDto {

    @NotBlank(message = "영화관 이름은 필수입니다.")
    private String theaterName;     // 영화관 이름 (필수)

    @NotBlank(message = "주소는 필수입니다.")
    private String address;         // 주소 (필수)

    private Double lat;             // 위도 (선택)
    private Double lng;             // 경도 (선택)

    @NotNull(message = "오픈 시간은 필수입니다.")
    private LocalTime openTime;     // 영업 시작 시간 (필수)

    @NotNull(message = "마감 시간은 필수입니다.")
    private LocalTime closeTime;    // 영업 종료 시간 (필수)
}
