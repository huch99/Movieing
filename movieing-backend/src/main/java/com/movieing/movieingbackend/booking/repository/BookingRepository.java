package com.movieing.movieingbackend.booking.repository;

import com.movieing.movieingbackend.booking.entity.Booking;
import com.movieing.movieingbackend.booking.entity.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByStatusNot(BookingStatus status, Pageable pageable);
}
