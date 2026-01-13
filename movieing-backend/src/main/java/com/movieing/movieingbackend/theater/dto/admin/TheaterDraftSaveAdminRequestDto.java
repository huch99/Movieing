package com.movieing.movieingbackend.theater.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TheaterDraftSaveAdminRequestDto {

    private String theaterName;
    private String address;
    private Double lat;
    private Double lng;
    private LocalTime openTime;
    private LocalTime closeTime;
}
