package com.movieing.movieingbackend.seat.dto;

import com.movieing.movieingbackend.seat.entity.SeatStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 좌석 생성 요청 DTO (관리자)
 *
 * - 상영관(Screen)에 좌석을 생성하기 위한 요청 DTO
 * - 좌석은 Screen에 설정된 seatRowCount × seatColCount 기준으로 일괄 생성된다.
 *
 * 사용 시나리오:
 * - ScreenDetail 페이지에서 "좌석 생성" 버튼 클릭
 * - Screen이 DRAFT → ACTIVE 전환 이후 좌석 최초 생성
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatCreateAdminRequestDto {

    @NotNull(message = "좌석 기본 상태는 필수입니다.")
    private SeatStatus status;   // 생성되는 좌석의 기본 상태 (보통 ACTIVE)

}
