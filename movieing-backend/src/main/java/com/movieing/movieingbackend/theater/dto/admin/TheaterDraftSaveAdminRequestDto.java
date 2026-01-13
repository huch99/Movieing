package com.movieing.movieingbackend.theater.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * 영화관 임시 저장(DRAFT) 요청 DTO (관리자)
 * <p>
 * - 영화관 초안 상태에서 부분 저장을 위해 사용하는 요청 DTO
 * - 모든 필드는 선택 입력이며, null 값은 "변경하지 않음"으로 처리
 * <p>
 * 사용 예:
 * - 관리자 화면에서 입력 중간중간 저장
 * - 단계별 입력 UI에서 필요한 값만 전달
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TheaterDraftSaveAdminRequestDto {

    private String theaterName;     // 영화관 이름 (선택)
    private String address;         // 주소 (선택)
    private Double lat;             // 위도 (선택)
    private Double lng;             // 경도 (선택)
    private LocalTime openTime;     // 영업 시작 시간 (선택)
    private LocalTime closeTime;    // 영업 종료 시간 (선택)
}
