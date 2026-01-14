package com.movieing.movieingbackend.seat.controller.admin;

import com.movieing.movieingbackend.aspect.ApiResponse;
import com.movieing.movieingbackend.seat.dto.SeatCreateAdminRequestDto;
import com.movieing.movieingbackend.seat.dto.SeatLayoutGenerateAdminRequestDto;
import com.movieing.movieingbackend.seat.dto.SeatLayoutItemAdminResponseDto;
import com.movieing.movieingbackend.seat.dto.SeatUpdateAdminRequestDto;
import com.movieing.movieingbackend.seat.service.admin.AdminSeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 좌석 관리 컨트롤러 (관리자)
 *
 * - ScreenDetail 페이지에서 좌석 생성/재생성 버튼으로 사용
 * - 좌석 삭제는 물리 삭제 정책
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/seats")
public class AdminSeatController {

    private final AdminSeatService adminSeatService;

    /**
     * 좌석 생성 (최초 생성)
     *
     * - screen의 seatRowCount × seatColCount 기준으로 좌석을 일괄 생성
     * - 이미 좌석이 존재하면 409 (재생성 API 사용)
     */
    @PostMapping("/{screenId}")
    public ApiResponse<Void> generateSeats(
            @PathVariable Long screenId,
            @Valid @RequestBody SeatCreateAdminRequestDto req
    ) {
        adminSeatService.generateSeats(screenId, req);
        return ApiResponse.success(null);
    }

    /**
     * 좌석 재생성
     *
     * - 기존 좌석을 모두 물리 삭제 후 다시 생성
     * - 예매 이력이 있으면 409
     */
    @PostMapping("/{screenId}/regenerate")
    public ApiResponse<Void> regenerateSeats(
            @PathVariable Long screenId,
            @Valid @RequestBody SeatCreateAdminRequestDto req
    ) {
        adminSeatService.regenerateSeats(screenId, req);
        return ApiResponse.success(null);
    }

    /**
     * 좌석 배치도 생성 (최초 생성)
     *
     * - Screen의 seatRowCount × seatColCount 기준으로 좌석을 일괄 생성
     * - 이미 좌석이 존재하면 409
     */
    @PostMapping("/{screenId}/layout")
    public ApiResponse<Void> generateSeatLayout(
            @PathVariable Long screenId,
            @Valid @RequestBody SeatCreateAdminRequestDto seatReq
    ) {
        adminSeatService.generateSeatLayout(
                screenId,
                SeatLayoutGenerateAdminRequestDto.builder()
                        .regenerate(false)
                        .build(),
                seatReq
        );
        return ApiResponse.success(null);
    }

    /**
     * 좌석 배치도 재생성
     *
     * - 기존 좌석을 모두 물리 삭제 후 다시 생성
     * - 예매 이력이 있으면 409
     */
    @PostMapping("/{screenId}/regenerate/layout")
    public ApiResponse<Void> regenerateSeatLayout(
            @PathVariable Long screenId,
            @Valid @RequestBody SeatCreateAdminRequestDto seatReq
    ) {
        adminSeatService.generateSeatLayout(
                screenId,
                SeatLayoutGenerateAdminRequestDto.builder()
                        .regenerate(true)
                        .build(),
                seatReq
        );
        return ApiResponse.success(null);
    }

    /**
     * 좌석 수정
     *
     * - 즉시 반영 (임시 저장 없음)
     * - 상태 변경 + 위치 변경을 한 번에 처리
     */
    @PutMapping("/{seatId}")
    public ApiResponse<Void> updateSeat(
            @PathVariable Long seatId,
            @Valid @RequestBody SeatUpdateAdminRequestDto req
    ) {
        adminSeatService.updateSeat(seatId, req);
        return ApiResponse.success(null);
    }

    /**
     * 좌석 삭제 (물리 삭제)
     *
     * - 예매 이력이 있는 좌석은 삭제 불가
     * - 삭제 후 Screen.capacity는 현재 좌석 수 기준으로 재계산
     */
    @DeleteMapping("/{seatId}")
    public ApiResponse<Void> deleteSeat(@PathVariable Long seatId) {
        adminSeatService.deleteSeat(seatId);
        return ApiResponse.success(null);
    }

    /**
     * 좌석 배치도 조회
     *
     * @param screenId 상영관 ID
     * @return 좌석 배치도 목록 (row/col 기준 정렬)
     */
    @GetMapping
    public ApiResponse<List<SeatLayoutItemAdminResponseDto>> getSeatLayout(
            @RequestParam Long screenId
    ) {
        return ApiResponse.success(adminSeatService.getSeatLayout(screenId));
    }
}
