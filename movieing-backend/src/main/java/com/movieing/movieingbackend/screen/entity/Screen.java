package com.movieing.movieingbackend.screen.entity;

import com.movieing.movieingbackend.aspect.BaseTimeEntity;
import com.movieing.movieingbackend.common.exception.ConflictException;
import com.movieing.movieingbackend.theater.entity.Theater;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Long screenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @Column(name = "screen_name", nullable = false, length = 255)
    private String screenName;

    @Column(name = "capacity", nullable = false)
    @Builder.Default
    private Integer capacity = 0;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ScreenStatus status = ScreenStatus.DRAFT;

    /* ======== Method ======== */
    public void changeScreenStatus(ScreenStatus nextStatus) {
        if (nextStatus == null) {
            throw new ConflictException("상영관 상태는 null일 수 없습니다.");
        }

        if (this.status == ScreenStatus.DELETED) {
            throw new IllegalStateException("삭제된 상영관은 상태 변경이 불가능합니다.");
        }

        this.status = nextStatus;
    }

    public void changeScreenName(String screenName) {
        if (this.status == ScreenStatus.DELETED) {
            throw new ConflictException("삭제된 상영관은 수정할 수 없습니다.");
        }

        if (screenName == null || screenName.trim().isEmpty()) {
            throw new IllegalArgumentException("상영관 이름은 필수입니다.");
        }

        this.screenName = screenName.trim();
    }

    public void changeCapacity(Integer capacity) {
        if (this.status == ScreenStatus.DELETED) {
            throw new ConflictException("삭제된 상영관은 수정할 수 없습니다.");
        }

        if (capacity == null || capacity < 0) {
            throw new IllegalArgumentException("수용 인원은 0 이상이어야 합니다.");
        }

        this.capacity = capacity;
    }

    public void changeTheater(Theater theater) {
        if (this.status == ScreenStatus.DELETED) {
            throw new ConflictException("삭제된 상영관은 영화관을 변경할 수 없습니다.");
        }

        if (theater == null) {
            throw new IllegalArgumentException("영화관은 필수입니다.");
        }

        this.theater = theater;
    }

    public void markDeleted() {
        this.status = ScreenStatus.DELETED;
    }
}
