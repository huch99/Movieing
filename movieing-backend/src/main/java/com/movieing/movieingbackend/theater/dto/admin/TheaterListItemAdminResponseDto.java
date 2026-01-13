package com.movieing.movieingbackend.theater.dto.admin;

import com.movieing.movieingbackend.theater.entity.TheaterStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TheaterListItemAdminResponseDto {

    private Long theaterId;
    private String theaterName;
    private String address;
    private Double lat;
    private Double lng;
    private LocalTime openTime;
    private LocalTime closeTime;
    private TheaterStatus status;
}
