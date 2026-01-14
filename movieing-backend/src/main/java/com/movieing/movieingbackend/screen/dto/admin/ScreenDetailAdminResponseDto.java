package com.movieing.movieingbackend.screen.dto.admin;

import com.movieing.movieingbackend.screen.entity.ScreenStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 상영관 상세 조회용 관리자 응답 DTO
 *
 * - 관리자 상영관 상세/수정 화면에 사용
 * - 초안(DRAFT) 상태 포함, 상영관의 모든 관리 대상 정보를 제공
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreenDetailAdminResponseDto {

    private Long screenId;          // 상영관 ID
    private Long theaterId;         // 소속 영화관 ID
    private String screenName;      // 상영관 이름
    private Integer capacity;       // 수용 인원
    private ScreenStatus status;    // 상영관 상태
    private Integer seatRowCount;   // 좌석 열
    private Integer seatColCount;   // 좌석 행
}
