package com.movieing.movieingbackend.movie.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieStatsAdminResponseDto {
    
    private Long totalMovies;    // 등록된 영화
    private Long showingMovies;  // 상영중인 영화
    private Long draftMovies;    // 작성중인 영화
    private Long endedMovies;    // 상영 종료 영화
    private Long hiddenMovies;   // 숨김 처리된 영화

    private String topBookedMovie;  // 누적예매 탑 영화
    private Long topBookedMovieCount;    // 누적 예매 건수
    private String topRevenueMovie; // 매출 탑 영화
    private Double topRevenueMovieAmount;   // 매출
    private Long todayBookedMovies;  // 오늘 예매 발생된 영화
    private Long endingSoonMovies;   // 상영 종료 임박 영화
}
