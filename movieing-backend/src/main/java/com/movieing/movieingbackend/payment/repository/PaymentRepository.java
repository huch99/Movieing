package com.movieing.movieingbackend.payment.repository;

import com.movieing.movieingbackend.movie.entity.MovieStatus;
import com.movieing.movieingbackend.movie.repository.TopRevenueMovieProjection;
import com.movieing.movieingbackend.payment.entity.Payment;
import com.movieing.movieingbackend.payment.entity.PaymentStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("""
        select m.title as title,
            sum(p.amount) as amount
        from Payment p
        join p.booking b
        join b.schedule s
        join s.movie m
        where p.status = :paidStatus
            and m.status in :movieStatuses
        group by m.movieId, m.title
        order by sum(p.amount) desc
    """)
    List<TopRevenueMovieProjection> findTopRevenueMovie(
            @Param("paidStatus") PaymentStatus paidStatus,
            @Param("movieStatuses")Collection<MovieStatus> movieStatuses,
            Pageable pageable
    );

    @Query("""
        select count(distinct m.movieId)
        from Payment p
        join p.booking b
        join b.schedule s
        join s.movie m
        where p.status = :paidStatus
            and p.createdAt >= :start
            and p.createdAt < :end
            and m.status in :movieStatuses
    """)
    Long countTodayBookedMovies(
            @Param("paidStatus") PaymentStatus paidStatus,
            @Param("start")LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("movieStatuses") Collection<MovieStatus> movieStatuses
    );
}
