package com.movieing.movieingbackend.theater.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TheaterStatsAdminResponseDto {

    private Long totalTheaters;
    private Long activeTheaters;
    private Long totalScreens;
    private Long activeScreens;
    private Long totalSeats;
    private Long activeSeats;
}
