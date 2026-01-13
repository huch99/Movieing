package com.movieing.movieingbackend.theater.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TheaterCompleteAdminRequestDto {

    @NotBlank(message = "영화관 이름은 필수입니다.")
    private String theaterName;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    private Double lat;
    private Double lng;

    @NotNull(message = "오픈 시간은 필수입니다.")
    private LocalTime openTime;

    @NotNull(message = "마감 시간은 필수입니다.")
    private LocalTime closeTime;
}
