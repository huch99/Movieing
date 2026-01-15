package com.movieing.movieingbackend.schedule.dto;

import com.movieing.movieingbackend.schedule.entity.ScheduleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 스케줄 상세 조회 응답 DTO
 *
 * <p>
 * Admin 스케줄 관리 페이지에서
 * 단일 스케줄의 상세 정보를 조회할 때 사용된다.
 * </p>
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDetailAdminResponseDto {

    private Long scheduleId;          // 스케줄 ID

    private Long movieId;             // 영화 ID
    private String title;        // 영화 제목
    private Integer runtimeMin;       // 상영 시간(분)

    private LocalDate scheduledDate;  // 상영 날짜
    private LocalTime startAt;        // 시작 시간
    private LocalTime endAt;          // 종료 시간 (자동 계산 결과)

    private ScheduleStatus status;    // 스케줄 상태
}
