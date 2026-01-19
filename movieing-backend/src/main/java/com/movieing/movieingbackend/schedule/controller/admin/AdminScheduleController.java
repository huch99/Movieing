package com.movieing.movieingbackend.schedule.controller.admin;

import com.movieing.movieingbackend.movie.dto.admin.MovieListItemAdminResponseDto;
import com.movieing.movieingbackend.movie.entity.MovieStatus;
import com.movieing.movieingbackend.movie.service.admin.AdminMovieService;
import com.movieing.movieingbackend.schedule.dto.*;
import com.movieing.movieingbackend.schedule.service.admin.AdminScheduleService;
import com.movieing.movieingbackend.screen.dto.admin.ScreenListItemAdminResponseDto;
import com.movieing.movieingbackend.screen.entity.ScreenStatus;
import com.movieing.movieingbackend.screen.service.admin.AdminScreenService;
import com.movieing.movieingbackend.theater.dto.admin.TheaterListItemAdminResponseDto;
import com.movieing.movieingbackend.theater.entity.Theater;
import com.movieing.movieingbackend.theater.entity.TheaterStatus;
import com.movieing.movieingbackend.theater.service.admin.AdminTheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin 스케줄 관리 컨트롤러
 */
@RestController
@RequestMapping("/api/admin/schedules")
@RequiredArgsConstructor
public class AdminScheduleController {

    private final AdminScheduleService adminScheduleService;
    private final AdminTheaterService adminTheaterService;
    private final AdminMovieService adminMovieService;
    private final AdminScreenService adminScreenService;

    /**
     * 영화관 목록 조회 (ACTIVE, HIDDEN)
     */
    @GetMapping("/theaters")
    public List<TheaterListItemAdminResponseDto> getTheaters() {
        return adminTheaterService.getListByStatuses(
                List.of(TheaterStatus.ACTIVE, TheaterStatus.HIDDEN)
        );
    }

    /**
     * 상영관 목록 조회 (ACTIVE)
     *
     * */
    @GetMapping("/{theaterId}/screens")
    public List<ScreenListItemAdminResponseDto> getScreens(@PathVariable Long theaterId) {

        return adminScreenService.getListByStatus(
                theaterId,
                List.of(ScreenStatus.ACTIVE)
        );
    }

    /**
     * 영화 목록 조회 (스케줄 등록용)
     *
     * <p>
     * status=COMING_SOON,NOW_SHOWING 형태로 전달받아
     * 해당 상태의 영화만 조회한다.
     * </p>
     */
    @GetMapping("/movies")
    public List<MovieListItemAdminResponseDto> getMovies(
            @RequestParam(name = "statuses", required = false) String statuses
    ) {
        // 기본값: 스케줄 등록 가능한 상태만
        List<MovieStatus> list = adminMovieService.parseStatusesOrDefault(
                statuses,
                List.of(MovieStatus.COMMING_SOON, MovieStatus.NOW_SHOWING)
        );

        return adminMovieService.getListByStatuses(list);
    }

    /**
     * 스케줄 임시 저장 (DRAFT)
     */
    @PostMapping
    public Long createDraft(@RequestBody ScheduleDraftSaveAdminRequestDto dto) {
        return adminScheduleService.createDraft(dto);
    }

    @PutMapping("/{scheduleId}/draft")
    public void saveDraft(
            @PathVariable Long scheduleId,
            @RequestBody ScheduleDraftSaveAdminRequestDto dto
    ) {
        adminScheduleService.saveDraft(scheduleId, dto);
    }


    /**
     * 스케줄 등록 완료 (OPEN)
     */
    @PostMapping("/{scheduleId}/complete")
    public void complete(
            @PathVariable Long scheduleId,
            @RequestBody ScheduleCompleteAdminRequestDto dto
    ) {
        adminScheduleService.complete(scheduleId, dto);
    }

    /**
     * 스케줄 수정 (OPEN 상태)
     */
    @PutMapping("/{scheduleId}")
    public void update(
            @PathVariable Long scheduleId,
            @RequestBody ScheduleUpdateAdminRequestDto dto
    ) {
        adminScheduleService.update(scheduleId, dto);
    }

    /**
     * 스케줄 상세 조회
     */
    @GetMapping("/{scheduleId}")
    public ScheduleDetailAdminResponseDto getDetail(@PathVariable Long scheduleId) {
        return adminScheduleService.getDetail(scheduleId);
    }

    /**
     * 스케줄 목록 조회 (페이징)
     */
    @GetMapping
    public Page<ScheduleListItemAdminResponseDto> getList(Pageable pageable) {
        return adminScheduleService.getList(pageable);
    }

    /**
     * 스케줄 취소
     */
    @PostMapping("/{scheduleId}/cancel")
    public void cancel(@PathVariable Long scheduleId) {
        adminScheduleService.cancel(scheduleId);
    }

    /**
     * 스케줄 소프트 삭제
     */
    @DeleteMapping("/{scheduleId}")
    public void delete(@PathVariable Long scheduleId) {
        adminScheduleService.delete(scheduleId);
    }
}
