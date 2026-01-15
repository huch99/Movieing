package com.movieing.movieingbackend.schedule.service.admin;

import com.movieing.movieingbackend.common.exception.NotFoundException;
import com.movieing.movieingbackend.movie.entity.Movie;
import com.movieing.movieingbackend.movie.repository.MovieRepository;
import com.movieing.movieingbackend.schedule.dto.*;
import com.movieing.movieingbackend.schedule.entity.Schedule;
import com.movieing.movieingbackend.schedule.entity.ScheduleStatus;
import com.movieing.movieingbackend.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MovieRepository movieRepository;

    /**
     * 스케줄 임시 저장 (DRAFT)
     */
    @Transactional
    public Long saveDraft(ScheduleDraftSaveAdminRequestDto dto) {
        Movie movie = null;
        if(dto.getMovieId() != null) {
            movie = movieRepository.findById(dto.getMovieId())
                    .orElseThrow(() -> new NotFoundException("영화를 찾을 수 없습니다."));
        }

        Schedule schedule = Schedule.createDraft(
                movie,
                dto.getScheduledDate(),
                dto.getStartAt()
        );

        return scheduleRepository.save(schedule).getScheduleId();
    }

    /**
     * 스케줄 등록 완료 (OPEN)
     */
    @Transactional
    public void complete(Long scheduleId, ScheduleCompleteAdminRequestDto dto) {
        Schedule schedule = getSchedule(scheduleId);

        Movie movie = movieRepository.findById(dto.getMovieId())
                .orElseThrow(() -> new NotFoundException("영화를 찾을 수 없습니다."));

        schedule.update(
                movie,
                dto.getScheduledDate(),
                dto.getStartAt()
        );
        schedule.complete();
    }

    /**
     * 스케줄 수정 (OPEN 상태에서만)
     */
    @Transactional
    public void update(Long scheduleId, ScheduleUpdateAdminRequestDto dto) {
        Schedule schedule = getSchedule(scheduleId);

        Movie movie = movieRepository.findById(dto.getMovieId())
                .orElseThrow(() -> new NotFoundException("영화를 찾을 수 없습니다."));

        schedule.update(
                movie,
                dto.getScheduledDate(),
                dto.getStartAt()
        );
    }

    /**
     * 스케줄 상세 조회
     */
    public ScheduleDetailAdminResponseDto getDetail(Long scheduleId) {
        Schedule schedule = getSchedule(scheduleId);

        return ScheduleDetailAdminResponseDto.builder()
                .scheduleId(schedule.getScheduleId())
                .movieId(schedule.getMovie() != null ? schedule.getMovie().getMovieId() : null)
                .title(schedule.getMovie() != null ? schedule.getMovie().getTitle() : null)
                .runtimeMin(schedule.getMovie() != null ? schedule.getMovie().getRuntimeMin() : null)
                .scheduledDate(schedule.getScheduledDate())
                .startAt(schedule.getStartAt())
                .endAt(schedule.getEndAt())
                .status(schedule.getStatus())
                .build();
    }

    /**
     * 스케줄 목록 조회 (페이징)
     */
    public Page<ScheduleListItemAdminResponseDto> getList(Pageable pageable) {
        return scheduleRepository.findByStatusNot(ScheduleStatus.DELETED, pageable)
                .map(schedule -> ScheduleListItemAdminResponseDto.builder()
                        .scheduleId(schedule.getScheduleId())
                        .movieId(schedule.getMovie() != null ? schedule.getMovie().getMovieId() : null)
                        .title(schedule.getMovie() != null ? schedule.getMovie().getTitle() : null)
                        .runtimeMin(schedule.getMovie() != null ? schedule.getMovie().getRuntimeMin() : null)
                        .scheduledDate(schedule.getScheduledDate())
                        .startAt(schedule.getStartAt())
                        .endAt(schedule.getEndAt())
                        .status(schedule.getStatus())
                        .build());
    }

    /**
     * 스케줄 취소
     */
    @Transactional
    public void cancel(Long scheduleId) {
        Schedule schedule = getSchedule(scheduleId);
        schedule.cancel();
    }

    /**
     * 스케줄 소프트 삭제
     */
    @Transactional
    public void delete(Long scheduleId) {
        Schedule schedule = getSchedule(scheduleId);
        schedule.softDelete();
    }


//    헬퍼
    private Schedule getSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("스케줄을 찾을 수 없습니다."));
    }

}
