package com.movieing.movieingbackend.booking.service.admin;

import com.movieing.movieingbackend.booking.dto.admin.BookingDetailAdminResponseDto;
import com.movieing.movieingbackend.booking.dto.admin.BookingListItemAdminResponseDto;
import com.movieing.movieingbackend.booking.entity.Booking;
import com.movieing.movieingbackend.booking.entity.BookingStatus;
import com.movieing.movieingbackend.booking.repository.BookingRepository;
import com.movieing.movieingbackend.booking_seat.entity.BookingSeat;
import com.movieing.movieingbackend.booking_seat.repository.BookingSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminBookingService {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;

    @Transactional
    public void cancelBooking(Long bookingId) {
        bookingRepository.findById(bookingId)
                .ifPresent(Booking::cancel);
    }

    @Transactional(readOnly = true)
    public Page<BookingListItemAdminResponseDto> getList(Pageable pageable) {
        return bookingRepository.findByStatusNot(BookingStatus.FAILED, pageable)
                .map(booking -> BookingListItemAdminResponseDto.builder()
                        .bookingId(booking.getBookingId())
                        .userId(booking.getUser() != null ? booking.getUser().getUserId() : null)
                        .userName(booking.getUser() != null ? booking.getUser().getUserName() : null)
                        .scheduleId(booking.getSchedule() != null ? booking.getSchedule().getScheduleId() : null)
                        .title(booking.getSchedule().getMovie() != null ? booking.getSchedule().getMovie().getTitle() : null)
                        .bookingNo(booking.getBookingNo())
                        .status(booking.getStatus())
                        .totalAmount(booking.getTotalAmount())
                        .build());
    }

    @Transactional(readOnly = true)
    public BookingDetailAdminResponseDto getDetail(Long bookingId) {
        Booking booking = getBooking(bookingId);
        BookingSeat bookingSeat = getBookingSeat(bookingId);

        return BookingDetailAdminResponseDto.builder()
                .title(booking.getSchedule().getMovie().getTitle())
                .theaterName(booking.getSchedule().getScreen().getTheater().getTheaterName())
                .screenName(booking.getSchedule().getScreen().getScreenName())
                .scheduledDate(booking.getSchedule().getScheduledDate())
                .startAt(booking.getSchedule().getStartAt())
                .endAt(booking.getSchedule().getEndAt())
                .bookingNo(booking.getBookingNo())
                .userName(booking.getUser().getUserName())
                .email(booking.getUser().getEmail())
                .phone(booking.getUser().getPhone())
                .totalAmount(booking.getTotalAmount())
                .state(booking.getStatus().toString())
                .seatNo(bookingSeat.getSeat().getSeatRow() + bookingSeat.getSeat().getSeatCol())
                .build();
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("예약 내역을 찾을 수 없습니다."));
    }

    private BookingSeat getBookingSeat(Long bookingId) {
        return bookingSeatRepository.findByBooking_BookingId(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("좌석 정보를 찾을 수 없습니다."));
    }
}
