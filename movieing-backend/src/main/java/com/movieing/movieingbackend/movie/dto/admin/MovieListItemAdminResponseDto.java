package com.movieing.movieingbackend.movie.dto.admin;

import com.movieing.movieingbackend.movie.entity.Movie;
import com.movieing.movieingbackend.movie.entity.MovieStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 영화 목록(Admin) 아이템 응답 DTO
 *
 * - Admin 영역에서 영화 목록 조회 시 사용되는 단일 아이템 모델
 * - 목록 화면에서 필요한 최소 정보만 포함
 * - 상세 조회는 MovieDetailAdminResponseDto 사용
 */
@Getter
@AllArgsConstructor
@Builder
public class MovieListItemAdminResponseDto {

    private Long movieId;          // 영화 ID (내부 식별자)
    private String title;          // 영화 제목
    private String posterUrl;      // 포스터 이미지 URL
    private LocalDate releaseDate; // 개봉일
    private LocalDate endDate;     // 상영 종료일
    private MovieStatus status;    // 영화 상태

    /**
     * Movie 엔티티 → Admin 영화 목록 아이템 DTO 변환
     *
     * - Admin 목록 화면에서 사용하기 위한 간략 정보 구성
     */
    public static MovieListItemAdminResponseDto from(Movie movie) {
        return MovieListItemAdminResponseDto.builder()
                .movieId(movie.getMovieId())
                .title(movie.getTitle())
                .posterUrl(movie.getPosterUrl())
                .releaseDate(movie.getReleaseDate())
                .endDate(movie.getEndDate())
                .status(movie.getStatus())
                .build();
    }
}
