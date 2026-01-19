package com.movieing.movieingbackend.booking.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDetailAdminResponseDto {

    private String title;
    private String theaterName;
    private String screenName;
    private LocalDate scheduledDate;
    private LocalTime startAt;
    private LocalTime endAt;
    private String bookingNo;
    private String userName;
    private String email;
    private String phone;
    private Double totalAmount;
    private String state;
    private String seatNo;
}
