package com.movieing.movieingbackend.screen.entity;

import com.movieing.movieingbackend.aspect.BaseTimeEntity;
import com.movieing.movieingbackend.common.exception.BadRequestException;
import com.movieing.movieingbackend.common.exception.ConflictException;
import com.movieing.movieingbackend.theater.entity.Theater;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 상영관(Screen) 엔티티
 *
 * - 영화관(Theater) 하위에 소속되는 상영관(관)을 표현
 * - Admin에서 초안 생성(DRAFT) → 활성(ACTIVE 등) 전환 흐름을 지원
 * - 물리 삭제 대신 status=DELETED 소프트 삭제 패턴 적용
 *
 * 인덱스:
 * - theater_id: 특정 영화관의 상영관 목록 조회 최적화
 * - status: 상태별 조회/필터링 최적화
 * - screen_name: 이름 기반 검색/중복검증/정렬 대비
 */
@Entity
@Table(name = "screen",
        indexes = {
                @Index(name = "idx_screen_theater_id", columnList = "theater_id"),
                @Index(name = "idx_screen_status", columnList = "status"),
                @Index(name = "idx_screen_name", columnList = "screen_name")
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Screen extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "screen_id", nullable = false)
    private Long screenId; // 상영관 ID (내부 PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater; // 소속 영화관

    @Column(name = "screen_name", nullable = false, length = 255)
    private String screenName; // 상영관 이름 (예: 1관, IMAX관 등)

    @Column(name = "capacity", nullable = false)
    @Builder.Default
    private Integer capacity = 0; // 수용 인원 (기본 0, 운영 정책에 따라 0 허용/불허 결정)

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ScreenStatus status = ScreenStatus.DRAFT; // 상영관 상태

    /* ======== Domain Methods ======== */

    /**
     * 상영관 상태 변경
     *
     * 정책:
     * - nextStatus는 null 불가
     * - DELETED 상태인 경우 변경 불가
     * - 삭제 전환은 markDeleted()로만 수행(의도/정책 명확화)
     */
    public void changeScreenStatus(ScreenStatus nextStatus) {
        if (nextStatus == null) {
            throw new BadRequestException("상영관 상태는 필수입니다.");
        }
        if (this.status == ScreenStatus.DELETED) {
            throw new ConflictException("삭제된 상영관은 상태 변경이 불가능합니다.");
        }
        if (nextStatus == ScreenStatus.DELETED) {
            throw new ConflictException("삭제 처리는 markDeleted()를 통해서만 가능합니다.");
        }

        this.status = nextStatus;
    }

    /**
     * 상영관 이름 변경
     *
     * 정책:
     * - DELETED 상태는 수정 불가
     * - 공백/빈 문자열 불가
     */
    public void changeScreenName(String screenName) {
        ensureNotDeleted();

        if (screenName == null || screenName.trim().isEmpty()) {
            throw new BadRequestException("상영관 이름은 필수입니다.");
        }

        this.screenName = screenName.trim();
    }

    /**
     * 수용 인원 변경
     *
     * 정책:
     * - DELETED 상태는 수정 불가
     * - 0 이상만 허용
     */
    public void changeCapacity(Integer capacity) {
        ensureNotDeleted();

        if (capacity == null || capacity < 0) {
            throw new BadRequestException("수용 인원은 0 이상이어야 합니다.");
        }

        this.capacity = capacity;
    }

    /**
     * 소속 영화관 변경
     *
     * 정책:
     * - DELETED 상태는 수정 불가
     * - theater는 null 불가
     */
    public void changeTheater(Theater theater) {
        ensureNotDeleted();

        if (theater == null) {
            throw new BadRequestException("영화관은 필수입니다.");
        }

        this.theater = theater;
    }

    /**
     * 소프트 삭제 처리
     *
     * - 물리 삭제 대신 DELETED 상태로 전환
     * - 삭제 이후에는 수정/상태 변경 불가(도메인 메서드에서 방어)
     */
    public void markDeleted() {
        this.status = ScreenStatus.DELETED;
    }

    private void ensureNotDeleted() {
        if (this.status == ScreenStatus.DELETED) {
            throw new ConflictException("삭제된 상영관은 수정할 수 없습니다.");
        }
    }
}
