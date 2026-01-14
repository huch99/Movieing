package com.movieing.movieingbackend.seat.dto;

import com.movieing.movieingbackend.seat.entity.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 좌석 배치도 조회용 응답 DTO
 *
 * - 상영관(Screen) 좌석 배치도를 그리기 위한 최소 정보
 * - row / col 기준으로 정렬된 리스트 형태로 사용
 *
 * 사용 시나리오:
 * - ScreenDetail 페이지 좌석 배치도 렌더링
 * - 좌석 클릭 → 상태 변경 / 삭제 대상 지정
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatLayoutItemAdminResponseDto {

    private Long seatId;          // 좌석 ID
    private String seatRow;       // 좌석 행 (A, B, C, ...)
    private Integer seatCol;      // 좌석 열 (1, 2, 3, ...)
    private SeatStatus status;    // 좌석 상태
}
