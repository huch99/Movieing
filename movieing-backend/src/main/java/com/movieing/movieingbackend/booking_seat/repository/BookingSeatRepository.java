package com.movieing.movieingbackend.booking_seat.repository;

import com.movieing.movieingbackend.booking_seat.entity.BookingSeat;
import com.movieing.movieingbackend.movie.entity.MovieStatus;
import com.movieing.movieingbackend.movie.repository.TopBookedMovieProjection;
import com.movieing.movieingbackend.payment.entity.PaymentStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {

    /**
     * 특정 상영관(screen)에 속한 좌석 중
     * 예매 이력이 하나라도 존재하는지 확인
     */
    @Query("""
        select count(bs) > 0
        from BookingSeat bs
        join bs.seat s
        where s.screen.screenId = :screenId
    """)
    boolean existsByScreenId(@Param("screenId") Long screenId);

    boolean existsBySeat_SeatId(Long seatId);

    Optional<BookingSeat> findByBooking_BookingId(Long bookingId);

    @Query("""
        select m.title as title,
            count(bs.bookingSeatId) as seatCount
        from BookingSeat bs
        join bs.booking b
        join b.schedule s
        join s.movie m
        join Payment p on p.booking = b
        where p.status = :paidStatus
            and m.status in :movieStatuses
        group by m.movieId, m.title
        order by count(bs.bookingSeatId) desc
    """)
    List<TopBookedMovieProjection> findTopBookedMovie(
            @Param("paidStatus") PaymentStatus paidStatus,
            @Param("movieStatuses") Collection<MovieStatus> movieStatuses,
            Pageable pageable);
}
