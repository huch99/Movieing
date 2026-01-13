package com.movieing.movieingbackend.theater.dto.admin;

import com.movieing.movieingbackend.theater.entity.TheaterStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * 영화관 상세 조회용 관리자 응답 DTO
 * <p>
 * - 관리자 영화관 상세/수정 화면에 사용
 * - 초안(DRAFT) 상태 포함, 영화관의 모든 관리 대상 정보를 제공
 * <p>
 * 사용 예:
 * - 영화관 상세 페이지 최초 진입 시 데이터 로딩
 * - 임시 저장 후 최신 상태 재조회
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TheaterDetailAdminResponseDto {

    private Long theaterId;         // 영화관 ID
    private TheaterStatus status;   // 영화관 상태
    private String theaterName;     // 영화관 이름
    private String address;         // 주소
    private Double lat;             // 위도
    private Double lng;             // 경도
    private LocalTime openTime;     // 영업 시작 시간
    private LocalTime closeTime;    // 영업 종료 시간
}
