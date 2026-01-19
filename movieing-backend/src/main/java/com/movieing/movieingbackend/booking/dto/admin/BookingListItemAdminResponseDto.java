package com.movieing.movieingbackend.booking.dto.admin;

import com.movieing.movieingbackend.booking.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingListItemAdminResponseDto {

    private Long bookingId;
    private Long userId;
    private String userName;
    private Long scheduleId;
    private String title;
    private String bookingNo;
    private BookingStatus status;
    private Double totalAmount;
}
