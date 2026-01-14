package com.movieing.movieingbackend.seat.service.admin;

import com.movieing.movieingbackend.booking_seat.repository.BookingSeatRepository;
import com.movieing.movieingbackend.common.exception.BadRequestException;
import com.movieing.movieingbackend.common.exception.ConflictException;
import com.movieing.movieingbackend.common.exception.NotFoundException;
import com.movieing.movieingbackend.screen.entity.Screen;
import com.movieing.movieingbackend.screen.entity.ScreenStatus;
import com.movieing.movieingbackend.screen.repository.ScreenRepository;
import com.movieing.movieingbackend.seat.dto.SeatCreateAdminRequestDto;
import com.movieing.movieingbackend.seat.dto.SeatLayoutGenerateAdminRequestDto;
import com.movieing.movieingbackend.seat.dto.SeatLayoutItemAdminResponseDto;
import com.movieing.movieingbackend.seat.dto.SeatUpdateAdminRequestDto;
import com.movieing.movieingbackend.seat.entity.Seat;
import com.movieing.movieingbackend.seat.entity.SeatStatus;
import com.movieing.movieingbackend.seat.respository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminSeatService {

    private final SeatRepository seatRepository;
    private final ScreenRepository screenRepository;
    private final BookingSeatRepository bookingSeatRepository;

    /**
     * 좌석 생성 (최초 생성)
     *
     * - screen의 seatRowCount × seatColCount 기준으로 좌석을 일괄 생성한다.
     * - 이미 좌석이 존재하면 생성 불가 (재생성 API 사용)
     */
    @Transactional
    public void generateSeats(Long screenId, SeatCreateAdminRequestDto req) {
        Screen screen = getScreen(screenId);
        validateScreenSeatSpec(screen);

        SeatStatus status = req.getStatus();
        if (status == null) {
            throw new BadRequestException("좌석 기본 상태는 필수입니다.");
        }

        long existing = seatRepository.countByScreen_ScreenId(screenId);
        if (existing > 0) {
            throw new ConflictException("이미 생성된 좌석이 있습니다. 재생성을 사용하세요.");
        }

        List<Seat> seats = buildSeats(screen, status);
        seatRepository.saveAll(seats);

        // capacity 정책: "현재 좌석 수"로 맞춤
        screen.changeCapacity(seats.size());
    }

    /**
     * 좌석 재생성
     *
     * - 기존 좌석을 모두 물리 삭제하고 다시 생성한다.
     * - 예매 이력이 있는 좌석이 하나라도 있으면 재생성 불가
     */
    @Transactional
    public void regenerateSeats(Long screenId, SeatCreateAdminRequestDto req) {
        Screen screen = getScreen(screenId);
        validateScreenSeatSpec(screen);

        SeatStatus status = req.getStatus();
        if (status == null) {
            throw new BadRequestException("좌석 기본 상태는 필수입니다.");
        }

        // ✅ 예매 이력 체크(정책에 맞게 구현)
        // 예: screenId에 속한 seat 중 booking_seat가 하나라도 있으면 true
        boolean hasBooking = bookingSeatRepository.existsByScreenId(screenId);
        if (hasBooking) {
            throw new ConflictException("예매 이력이 있는 상영관은 좌석 재생성을 할 수 없습니다.");
        }

        // 기존 좌석 전부 삭제(물리)
        seatRepository.deleteByScreen_ScreenId(screenId);

        // 재생성
        List<Seat> seats = buildSeats(screen, status);
        seatRepository.saveAll(seats);

        screen.changeCapacity(seats.size());
    }

    /**
     * 좌석 배치도 생성/재생성
     *
     * - Screen의 seatRowCount × seatColCount 기준으로 좌석을 일괄 생성한다.
     * - regenerate=false:
     *    - 기존 좌석이 있으면 409
     * - regenerate=true:
     *    - 예매 이력이 있으면 409
     *    - 기존 좌석 전체 물리 삭제 후 재생성
     *
     * - 생성 후 Screen.capacity는 생성된 좌석 수로 동기화한다.
     */
    @Transactional
    public void generateSeatLayout(
            Long screenId,
            SeatLayoutGenerateAdminRequestDto layoutReq,
            SeatCreateAdminRequestDto seatReq
    ) {
        Screen screen = getScreen(screenId);
        validateScreenSeatSpec(screen);

        // 기본 좌석 상태
        SeatStatus status = seatReq.getStatus();
        if (status == null) {
            throw new BadRequestException("좌석 기본 상태는 필수입니다.");
        }

        boolean regenerate = Boolean.TRUE.equals(layoutReq.getRegenerate());
        long existing = seatRepository.countByScreen_ScreenId(screenId);

        if (!regenerate) {
            if (existing > 0) {
                throw new ConflictException("이미 생성된 좌석이 있습니다. 재생성을 사용하세요.");
            }
        } else {
            // ✅ 예매 이력 체크(정책에 맞게 구현)
            boolean hasBooking = bookingSeatRepository.existsByScreenId(screenId);
            if (hasBooking) {
                throw new ConflictException("예매 이력이 있는 상영관은 좌석 재생성을 할 수 없습니다.");
            }
            if (existing > 0) {
                seatRepository.deleteByScreen_ScreenId(screenId);
            }
        }

        List<Seat> seats = buildSeats(screen, status);
        seatRepository.saveAll(seats);

        // capacity 정책: "현재 좌석 수"
        screen.changeCapacity(seats.size());
    }

    /**
     * 좌석 수정
     *
     * - 좌석 상태 변경
     * - 좌석 위치(row / col) 변경
     *
     * 정책:
     * - 즉시 반영 (임시 저장 없음)
     * - (screen_id, seat_row, seat_col) 유니크 제약 충돌 시 예외 발생
     * - 예매 이력이 있는 좌석은 위치 변경 불가 (상태 변경은 허용)
     */
    @Transactional
    public void updateSeat(Long seatId, SeatUpdateAdminRequestDto req) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new NotFoundException("좌석을 찾을 수 없습니다."));

        // ===== 상태 변경 =====
        SeatStatus newStatus = req.getStatus();
        if (newStatus == null) {
            throw new BadRequestException("좌석 상태는 필수입니다.");
        }
        seat.changeStatus(newStatus);

        // ===== 위치 변경 =====
        boolean positionChanged =
                !seat.getSeatRow().equalsIgnoreCase(req.getSeatRow())
                        || !seat.getSeatCol().equals(req.getSeatCol());

        if (positionChanged) {
            boolean hasBooking = bookingSeatRepository.existsBySeat_SeatId(seatId);
            if (hasBooking) {
                throw new ConflictException("예매 이력이 있는 좌석은 위치를 변경할 수 없습니다.");
            }
            seat.changePosition(req.getSeatRow(), req.getSeatCol());
        }
    }

    /**
     * 좌석 삭제 (물리 삭제)
     *
     * 정책:
     * - 좌석은 물리 삭제
     * - 예매 이력이 있는 좌석은 삭제 불가
     * - 삭제 후 상영관 capacity는 "현재 좌석 수" 기준으로 재계산
     */
    @Transactional
    public void deleteSeat(Long seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new NotFoundException("좌석을 찾을 수 없습니다."));

        // 예매 이력 체크
        boolean hasBooking = bookingSeatRepository.existsBySeat_SeatId(seatId);
        if (hasBooking) {
            throw new ConflictException("예매 이력이 있는 좌석은 삭제할 수 없습니다.");
        }

        // 소속 상영관
        var screen = seat.getScreen();

        // 물리 삭제
        seatRepository.delete(seat);

        // capacity 재계산 (현재 남은 좌석 수 기준)
        long remaining = seatRepository.countByScreen_ScreenId(screen.getScreenId());
        screen.changeCapacity((int) remaining);
    }

    /**
     * 좌석 배치도 조회
     *
     * - 특정 상영관(screenId)의 좌석 목록을 row/col 기준으로 정렬하여 반환
     * - ScreenDetail 페이지의 좌석 배치도 렌더링에 사용
     */
    public List<SeatLayoutItemAdminResponseDto> getSeatLayout(Long screenId) {
        List<Seat> seats = seatRepository.findByScreen_ScreenIdOrderBySeatRowAscSeatColAsc(screenId);

        if (seats == null) {
            throw new NotFoundException("좌석 정보를 찾을 수 없습니다.");
        }

        return seats.stream()
                .map(s -> SeatLayoutItemAdminResponseDto.builder()
                        .seatId(s.getSeatId())
                        .seatRow(s.getSeatRow())
                        .seatCol(s.getSeatCol())
                        .status(s.getStatus())
                        .build()
                )
                .toList();
    }

    /* ================= helpers ================= */

    private Screen getScreen(Long screenId) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new NotFoundException("상영관을 찾을 수 없습니다."));

        if (screen.getStatus() == ScreenStatus.DELETED) {
            throw new ConflictException("삭제된 상영관에는 좌석을 생성할 수 없습니다.");
        }
        return screen;
    }

    private void validateScreenSeatSpec(Screen screen) {
        Integer r = screen.getSeatRowCount();
        Integer c = screen.getSeatColCount();

        if (r == null || r <= 0) throw new BadRequestException("좌석 행 수(seatRowCount)가 올바르지 않습니다.");
        if (c == null || c <= 0) throw new BadRequestException("좌석 열 수(seatColCount)가 올바르지 않습니다.");

        // 필요하면 제한 추가(너무 큰 값 방지)
        // if (r > 200 || c > 200) throw new BadRequestException("좌석 행/열 수가 너무 큽니다.");
    }

    private List<Seat> buildSeats(Screen screen, SeatStatus defaultStatus) {
        int rowCount = screen.getSeatRowCount();
        int colCount = screen.getSeatColCount();

        List<Seat> seats = new ArrayList<>(rowCount * colCount);

        for (int r = 1; r <= rowCount; r++) {
            String rowLabel = toRowLabel(r); // 1->A, 26->Z, 27->AA ...
            for (int c = 1; c <= colCount; c++) {
                seats.add(Seat.create(screen, rowLabel, c, defaultStatus));
            }
        }
        return seats;
    }

    // 1 -> A, 2 -> B, ... 26 -> Z, 27 -> AA ...
    private String toRowLabel(int n) {
        if (n <= 0) throw new IllegalArgumentException("row index must be positive");
        StringBuilder sb = new StringBuilder();
        int x = n;
        while (x > 0) {
            x--; // 1-based to 0-based
            sb.insert(0, (char) ('A' + (x % 26)));
            x /= 26;
        }
        return sb.toString();
    }
}
