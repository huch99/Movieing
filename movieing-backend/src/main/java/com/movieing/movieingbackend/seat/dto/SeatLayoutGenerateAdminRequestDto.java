package com.movieing.movieingbackend.seat.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 좌석 배치도 생성/재생성 요청 DTO (관리자)
 *
 * - ScreenDetail 페이지에서 "좌석 생성 / 좌석 재생성" 버튼 클릭 시 사용
 * - 실제 좌석 데이터 생성은 Screen의 row/col 설정을 기준으로 서버에서 처리
 * - 이 DTO는 “배치도 생성 행위”에 대한 최소한의 제어 정보만 전달한다.
 *
 * 사용 시나리오:
 * - 최초 생성: regenerate = false
 * - 재생성: regenerate = true (기존 좌석 전부 물리 삭제 후 재생성)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatLayoutGenerateAdminRequestDto {

    @NotNull(message = "재생성 여부는 필수입니다.")
    private Boolean regenerate;    // true: 재생성, false: 최초 생성
}
