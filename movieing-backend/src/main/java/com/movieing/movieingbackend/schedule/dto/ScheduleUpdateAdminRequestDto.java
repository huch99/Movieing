package com.movieing.movieingbackend.schedule.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 스케줄 수정 요청 DTO
 *
 * <p>
 * OPEN 상태의 스케줄만 수정 가능하다.
 * 종료 시간(endAt)은 서버에서 영화 상영 시간(runtimeMin)을 기준으로 자동 재계산된다.
 * </p>
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleUpdateAdminRequestDto {

    @NotNull(message = "영화 선택은 필수입니다.")
    private Long movieId;            // 영화 ID

    @NotNull(message = "상영 날짜는 필수입니다.")
    private LocalDate scheduledDate; // 상영 날짜

    @NotNull(message = "상영 시작 시간은 필수입니다.")
    private LocalTime startAt;       // 상영 시작 시간
}
