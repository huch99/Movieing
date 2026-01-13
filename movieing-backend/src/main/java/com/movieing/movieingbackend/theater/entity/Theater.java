package com.movieing.movieingbackend.theater.entity;

import com.movieing.movieingbackend.aspect.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * 영화관(Theater) 엔티티
 * <p>
 * - 영화관 기본 정보를 표현하는 엔티티
 * - 관리자(Admin) 화면에서 생성/수정/상태 관리 대상
 * - 물리 삭제 대신 status 값으로 노출 여부 및 운영 상태를 관리
 * <p>
 * 특징:
 * - 초안(DRAFT) 상태로 생성 후, 완료 처리 시 운영 상태로 전환
 * - 위도/경도, 영업 시간 등은 선택 입력 값으로 설계
 * - JPA Auditing(BaseTimeEntity)을 통해 생성/수정 시간 자동 관리
 */
@Entity
@Table(name = "theater",
        indexes = {
                @Index(name = "idx_theater_status", columnList = "status"),
                @Index(name = "idx_theater_name", columnList = "theater_name")
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Theater extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theater_id", nullable = false)
    private Long theaterId;              // 영화관 내부 PK

    @Column(name = "theater_name", nullable = true, length = 255)
    private String theaterName;          // 영화관 이름

    @Column(name = "address", nullable = true, length = 255)
    private String address;              // 영화관 주소

    @Column(name = "lat", nullable = true)
    private Double lat;                  // 위도

    @Column(name = "lng", nullable = true)
    private Double lng;                  // 경도

    @Column(name = "open_time", nullable = true)
    private LocalTime openTime;          // 영업 시작 시간

    @Column(name = "close_time", nullable = true)
    private LocalTime closeTime;         // 영업 종료 시간

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TheaterStatus status = TheaterStatus.DRAFT;   // 영화관 상태

    /* ======== Domain Methods ======== */

    /**
     * 영화관 상태 변경
     */
    public void changeTheaterStatus(TheaterStatus theaterStatus) {
        this.status = theaterStatus;
    }

    /**
     * 영업 시작 시간 변경
     */
    public void changeOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    /**
     * 영업 종료 시간 변경
     */
    public void changeCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
    }

    /**
     * 위도 변경
     */
    public void changeLat(Double lat) {
        this.lat = lat;
    }

    /**
     * 경도 변경
     */
    public void changeLng(Double lng) {
        this.lng = lng;
    }

    /**
     * 영화관 이름 변경
     */
    public void changeTheaterName(String theaterName) {
        this.theaterName = theaterName;
    }

    /**
     * 영화관 주소 변경
     */
    public void changeAddress(String address) {
        this.address = address;
    }
}
