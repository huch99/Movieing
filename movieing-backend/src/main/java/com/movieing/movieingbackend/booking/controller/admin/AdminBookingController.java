package com.movieing.movieingbackend.booking.controller.admin;

import com.movieing.movieingbackend.booking.dto.admin.BookingDetailAdminResponseDto;
import com.movieing.movieingbackend.booking.dto.admin.BookingListItemAdminResponseDto;
import com.movieing.movieingbackend.booking.service.admin.AdminBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/bookings")
public class AdminBookingController {

    private final AdminBookingService adminBookingService;

    @GetMapping
    public Page<BookingListItemAdminResponseDto> getList(Pageable pageable) {
        return adminBookingService.getList(pageable);
    }

    @PutMapping("/{bookingId}/cancel")
    public void cancelBooking(@PathVariable Long bookingId) {
        adminBookingService.cancelBooking(bookingId);
    }

    @GetMapping("/{bookingId}/detail")
    public BookingDetailAdminResponseDto getDetail(@PathVariable Long bookingId) {
        return adminBookingService.getDetail(bookingId);
    }
}
