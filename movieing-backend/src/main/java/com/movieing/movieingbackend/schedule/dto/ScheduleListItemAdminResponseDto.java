package com.movieing.movieingbackend.schedule.dto;

import com.movieing.movieingbackend.schedule.entity.ScheduleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 스케줄 목록 조회용 응답 DTO
 *
 * <p>
 * Admin 스케줄 관리 페이지의 리스트 화면에서 사용된다.
 * 스케줄의 핵심 정보만 전달하며, 상세 정보는 별도 조회 API에서 처리한다.
 * </p>
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleListItemAdminResponseDto {
    Long scheduleId;      // 스케줄 ID

    Long movieId;             // 영화 ID
    Long screenId;
    String screenName;
    String title;      // 영화 제목
    Integer runtimeMin;       // 상영 시간(분)

    LocalDate scheduledDate;  // 상영 날짜
    LocalTime startAt;  // 시작 시간
    LocalTime endAt;        // 종료 시간 (자동 계산 결과)

    ScheduleStatus status;    // 스케줄 상태
}
