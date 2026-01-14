package com.movieing.movieingbackend.seat.entity;

import com.movieing.movieingbackend.aspect.BaseTimeEntity;
import com.movieing.movieingbackend.common.exception.BadRequestException;
import com.movieing.movieingbackend.common.exception.ConflictException;
import com.movieing.movieingbackend.screen.entity.Screen;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seat",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_seat_screen_row_col",
                        columnNames = {"screen_id", "seat_row", "seat_col"}
                )
        })
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Seat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id", nullable = false)
    private Long seatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @Column(name = "seat_row", nullable = false, length = 10)
    private String seatRow;

    @Column(name = "seat_col", nullable = false)
    private Integer seatCol;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private SeatStatus status = SeatStatus.ACTIVE;

    /* ======== Domain Methods ======== */

    /**
     * 좌석 생성 (팩토리 메서드)
     *
     * - 상영관(Screen)에 속한 좌석을 생성한다.
     * - seatRow / seatCol은 좌석의 물리적 위치를 의미한다.
     * - status는 기본적으로 ACTIVE를 사용하지만, 필요 시 다른 상태로 생성 가능
     *
     * 사용 시점:
     * - 상영관 좌석 자동 생성(row/col 기반)
     * - 관리자 좌석 수동 추가
     */
    public static Seat create(
            Screen screen,
            String seatRow,
            Integer seatCol,
            SeatStatus status
    ) {
        if (screen == null) {
            throw new BadRequestException("상영관은 필수입니다.");
        }
        if (seatRow == null || seatRow.trim().isEmpty()) {
            throw new BadRequestException("좌석 행은 필수입니다.");
        }
        if (seatCol == null || seatCol <= 0) {
            throw new BadRequestException("좌석 열은 1 이상이어야 합니다.");
        }

        return Seat.builder()
                .screen(screen)
                .seatRow(seatRow.trim().toUpperCase())
                .seatCol(seatCol)
                .status(status != null ? status : SeatStatus.ACTIVE)
                .build();
    }

    /**
     * 좌석 상태 변경
     *
     * - ACTIVE / INACTIVE / BROKEN / BLOCKED 상태로 변경 가능
     * - 좌석 클릭 토글 또는 관리자 설정 변경 시 사용
     *
     * 정책:
     * - status는 null 불가
     */
    public void changeStatus(SeatStatus status) {
        if (status == null) {
            throw new BadRequestException("좌석 상태는 필수입니다.");
        }
        this.status = status;
    }

    /**
     * 좌석 위치 변경 (배치 수정)
     *
     * - 좌석의 행(row)과 열(col)을 동시에 변경한다.
     * - 좌석 배치 재구성 시 사용된다.
     *
     * 주의:
     * - (screen_id, seat_row, seat_col) 유니크 제약 충돌 가능
     * - 충돌 여부는 서비스 레이어에서 사전 검증 권장
     */
    public void changePosition(String seatRow, Integer seatCol) {
        if (seatRow == null || seatRow.trim().isEmpty()) {
            throw new BadRequestException("좌석 행은 필수입니다.");
        }
        if (seatCol == null || seatCol <= 0) {
            throw new BadRequestException("좌석 열은 1 이상이어야 합니다.");
        }

        this.seatRow = seatRow.trim().toUpperCase();
        this.seatCol = seatCol;
    }

    /**
     * 좌석 행(Row) 변경
     *
     * - 좌석의 행 정보만 변경한다.
     * - 배치 수정 API를 세분화할 경우 사용
     */
    public void changeSeatRow(String seatRow) {
        if (seatRow == null || seatRow.trim().isEmpty()) {
            throw new BadRequestException("좌석 행은 필수입니다.");
        }
        this.seatRow = seatRow.trim().toUpperCase();
    }

    /**
     * 좌석 열(Col) 변경
     *
     * - 좌석의 열 정보만 변경한다.
     * - 배치 수정 API를 세분화할 경우 사용
     */
    public void changeSeatCol(Integer seatCol) {
        if (seatCol == null || seatCol <= 0) {
            throw new BadRequestException("좌석 열은 1 이상이어야 합니다.");
        }
        this.seatCol = seatCol;
    }

    /**
     * 좌석 삭제 가능 여부 검증
     *
     * - 좌석은 물리 삭제 정책을 사용한다.
     * - 예매 이력이 있는 좌석은 삭제할 수 없다.
     *
     * 사용 시점:
     * - 좌석 삭제 API 호출 시, delete 전에 검증
     */
    public void assertDeletable(boolean hasBooking) {
        if (hasBooking) {
            throw new ConflictException("예매 이력이 있는 좌석은 삭제할 수 없습니다.");
        }
    }

}
