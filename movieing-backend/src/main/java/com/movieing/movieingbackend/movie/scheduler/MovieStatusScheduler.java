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

/**
 * 영화 상태 자동 전이 스케줄러
 *
 * - 영화의 개봉일 / 종료일을 기준으로 상태를 자동 전환
 * - 수동(Admin) 조작 없이도 영화 상태가 날짜에 맞게 유지되도록 보조
 *
 * 상태 전이 규칙:
 * - COMING_SOON → NOW_SHOWING : releaseDate <= 오늘
 * - NOW_SHOWING → ENDED       : endDate < 오늘
 *
 * 주의:
 * - 실제 상태 변경은 Movie 엔티티의 도메인 메서드를 통해 수행
 * - 스케줄러는 "대상 조회 + 메서드 호출"만 담당
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MovieStatusScheduler {

    private final MovieRepository movieRepository;

    /**
     * 매일 00:10 실행 (서버 시간 기준)
     *
     * - 개봉일(releaseDate)이 오늘 이전(또는 오늘)인
     *   COMING_SOON 상태의 영화를 NOW_SHOWING 상태로 전환
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
     *
     * - 상영 종료일(endDate)이 오늘 이전인
     *   NOW_SHOWING 상태의 영화를 ENDED 상태로 전환
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
