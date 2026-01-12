package com.movieing.movieingbackend.movie.repository;

import com.movieing.movieingbackend.movie.entity.Movie;
import com.movieing.movieingbackend.movie.entity.MovieStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByStatusAndReleaseDateLessThanEqual(MovieStatus status, LocalDate date);

    List<Movie> findByStatusAndEndDateLessThan(MovieStatus status, LocalDate date);

    // 어드민 목록용 (DELETED 제외 같은 정책이 있으면 여기서 쓰기)
    List<Movie> findByStatusNot(MovieStatus status);
}
