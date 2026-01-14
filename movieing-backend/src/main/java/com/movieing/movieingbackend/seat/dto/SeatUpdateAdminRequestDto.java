package com.movieing.movieingbackend.seat.dto;

import com.movieing.movieingbackend.seat.entity.SeatStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 좌석 수정 요청 DTO (관리자)
 *
 * - 좌석은 즉시 생성 정책이므로 임시 저장 없음
 * - 수정은 "상태 변경"과 "위치 변경(배치 수정)"만 허용
 *
 * 사용 시나리오:
 * - 좌석 클릭 → 상태 변경
 * - 좌석 배치 편집 → row/col 이동
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatUpdateAdminRequestDto {

    @NotNull(message = "좌석 상태는 필수입니다.")
    private SeatStatus status;   // ACTIVE / INACTIVE / BROKEN / BLOCKED

    @NotNull(message = "좌석 행은 필수입니다.")
    private String seatRow;      // 좌석 행 (예: A, B, C ...)

    @NotNull(message = "좌석 열은 필수입니다.")
    private Integer seatCol;     // 좌석 열 (1 이상)
}
