package com.movieing.movieingbackend.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 스케줄 임시 저장(DRAFT) 요청 DTO
 *
 * <p>
 * Admin 스케줄 관리 페이지에서
 * 임시 생성 / 임시 저장 단계에서 사용된다.
 *
 * <p>
 * 종료 시간(endAt)은 서버에서
 * 영화의 상영 시간(runtimeMin)을 기준으로 자동 계산되므로
 * 요청 값에 포함하지 않는다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDraftSaveAdminRequestDto {
    private Long movieId;            // 영화 ID (임시 단계에서는 null 허용)
    private Long screenId;
    private LocalDate scheduledDate; // 상영 날짜
    private LocalTime startAt;       // 상영 시작 시간
}
