package com.movieing.movieingbackend.schedule.entity;

import com.movieing.movieingbackend.aspect.BaseTimeEntity;
import com.movieing.movieingbackend.movie.entity.Movie;
import com.movieing.movieingbackend.screen.entity.Screen;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 상영 스케줄 엔티티
 *
 * <p>
 * 특정 날짜(LocalDate)에 영화 상영 시간(LocalTime)을 관리한다.
 * 종료 시간(endAt)은 영화의 상영 시간(runtimeMin)을 기준으로
 * 시작 시간(startAt)에서 자동 계산된다.
 *
 * <p>
 * 상태 흐름:
 * DRAFT → OPEN → CLOSED
 * ↘ CANCELLED / DELETED
 */
@Entity
@Getter
@Table(name = "schedule")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;            // 스케줄 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = true)
    private Movie movie;                // 상영 영화 (임시 스케줄 단계에서는 null 가능)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = true)
    private Screen screen;

    @Column(name = "scheduled_date", nullable = true)
    private LocalDate scheduledDate;    // 상영 날짜

    @Column(name = "start_at", nullable = true)
    private LocalTime startAt;          // 상영 시작 시간

    @Column(name = "end_at", nullable = true)
    private LocalTime endAt;            // 상영 종료 시간

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ScheduleStatus status =  ScheduleStatus.DRAFT;  // 스케줄 상태

    /**
     * 임시 스케줄 생성 (DRAFT)
     *
     * <p>
     * 영화, 날짜, 시작 시간만 전달받고
     * 종료 시간은 영화 상영 시간(runtimeMin)을 기준으로 자동 계산된다.
     */
    public static Schedule createDraft(
            Movie movie,
            Screen screen,
            LocalDate scheduledDate,
            LocalTime startAt
    ) {
        Schedule schedule = Schedule.builder()
                .movie(movie)
                .screen(screen)
                .scheduledDate(scheduledDate)
                .startAt(startAt)
                .status(ScheduleStatus.DRAFT)
                .build();

        schedule.calculateEndAt();
        return schedule;
    }

    /**
     * 스케줄 완료 처리 (OPEN)
     *
     * <p>
     * 임시(DRAFT) 상태의 스케줄을 운영 상태로 전환한다.
     */
    public void complete() {
        this.status = ScheduleStatus.OPEN;
    }

    /**
     * 스케줄 수정
     *
     * <p>
     * OPEN 상태에서만 수정 가능하며,
     * 시작 시간이 변경되면 종료 시간도 자동 재계산된다.
     */
    public void update(
            Movie movie,
            Screen screen,
            LocalDate scheduledDate,
            LocalTime startAt
    ) {
        this.movie = movie;
        this.screen = screen;
        this.scheduledDate = scheduledDate;
        this.startAt = startAt;
        calculateEndAt();
    }

    /**
     * 스케줄 종료 처리 (CLOSED)
     */
    public void close() {
        this.status = ScheduleStatus.CLOSED;
    }

    /**
     * 스케줄 취소 처리 (CANCELLED)
     */
    public void cancel() {
        this.status = ScheduleStatus.CANCELED;
    }

    /**
     * 스케줄 소프트 삭제
     *
     * <p>
     * 물리 삭제 대신 DELETED 상태로 전환한다.
     */
    public void softDelete() {
        this.status = ScheduleStatus.DELETED;
    }

    /**
     * 종료 시간 자동 계산
     *
     * <p>
     * startAt + movie.runtimeMin(분)
     */
    private void calculateEndAt() {
        if (this.movie == null || this.movie.getRuntimeMin() == null || this.startAt == null) {
            this.endAt = null;
            return;
        }
        this.endAt = this.startAt.plusMinutes(this.movie.getRuntimeMin());
    }
}
