package com.movieing.movieingbackend.theater.entity;

import com.movieing.movieingbackend.aspect.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

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
    private Long theaterId;

    @Column(name = "theater_name", nullable = true, length = 255)
    private String theaterName;

    @Column(name = "address", nullable = true, length = 255)
    private String address;

    @Column(name = "lat", nullable = true)
    private Double lat;

    @Column(name = "lng", nullable = true)
    private Double lng;

    @Column(name = "open_time", nullable = true)
    private LocalTime openTime;

    @Column(name = "close_time", nullable = true)
    private LocalTime closeTime;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TheaterStatus status = TheaterStatus.DRAFT;

    /* ======== Method ======== */
    public void changeTheaterStatus(TheaterStatus theaterStatus) {
        this.status = theaterStatus;
    }

    public void changeOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    public void changeCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
    }

    public void changeLat(Double lat) {
        this.lat = lat;
    }

    public void changeLng(Double lng) {
        this.lng = lng;
    }

    public void changeTheaterName(String theaterName) {
        this.theaterName = theaterName;
    }

    public void changeAddress(String address) {
        this.address = address;
    }
}
