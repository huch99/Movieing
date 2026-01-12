package com.movieing.movieingbackend.movie.scheduler;

import com.movieing.movieingbackend.movie.entity.Movie;
import com.movieing.movieingbackend.movie.entity.MovieStatus;
import com.movieing.movieingbackend.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovieStatusScheduler {

    private final MovieRepository movieRepository;

    /**
     * 매일 00:10 실행 (서버 시간 기준)
     * - releaseDate <= today 인 COMING_SOON 영화를 NOW_SHOWING 으로 전환
     */
    @Transactional
    @Scheduled(cron = "0 10 0 * * *")
    public void startShowingReleasedMovies() {

        LocalDate today = LocalDate.now();
        List<Movie> targets =
                movieRepository.findByStatusAndReleaseDateLessThanEqual(MovieStatus.COMING_SOON, today);

        for (Movie movie : targets) {
            movie.startShowing();
        }

        if (!targets.isEmpty()) {
            log.info("MovieStatusScheduler: COMING_SOON -> NOW_SHOWING count={}", targets.size());
        }
    }

    /**
     * 매일 00:20 실행 (서버 시간 기준)
     * - endDate >= today 인 NOW_SHOWING 영화를 ENDED 으로 전환
     */
    @Transactional
    @Scheduled(cron = "0 20 0 * * *") // 매일 00:20
    public void endShowingMovies() {

        LocalDate today = LocalDate.now();

        List<Movie> targets =
                movieRepository.findByStatusAndEndDateLessThan(MovieStatus.NOW_SHOWING, today);

        for (Movie movie : targets) {
            movie.endShowing();
        }

        if (!targets.isEmpty()) {
            log.info("MovieStatusScheduler: NOW_SHOWING -> ENDED count={}", targets.size());
        }
    }
}
