package com.movieing.movieingbackend.theater.dto.admin;

import com.movieing.movieingbackend.theater.entity.TheaterStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * 영화관 목록 조회용 관리자 응답 DTO
 * <p>
 * - 관리자 영화관 관리 화면의 리스트에 사용
 * - 삭제(DELETED) 상태를 제외한 영화관 목록 정보를 표현
 * - 상세 정보 진입 전, 요약 정보 전달 목적
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TheaterListItemAdminResponseDto {

    private Long theaterId;         // 영화관 ID
    private String theaterName;     // 영화관 이름
    private String address;         // 주소
    private Double lat;             // 위도
    private Double lng;             // 경도
    private LocalTime openTime;     // 영업 시작 시간
    private LocalTime closeTime;    // 영업 종료 시간
    private TheaterStatus status;   // 영화관 상태
}
