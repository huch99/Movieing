package com.movieing.movieingbackend.movie.dto.admin;

import com.movieing.movieingbackend.movie.entity.Movie;
import com.movieing.movieingbackend.movie.entity.MovieStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 영화 상세 정보(Admin) 응답 DTO
 *
 * - Admin 영역에서 영화 상세 조회 시 사용되는 응답 모델
 * - 초안(DRAFT)부터 운영 상태까지 모든 상태의 영화 정보를 조회 가능
 * - 엔티티(Movie)를 직접 노출하지 않고 필요한 필드만 전달하기 위한 DTO
 */
@Getter
@AllArgsConstructor
@Builder
public class MovieDetailAdminResponseDto {

    private Long movieId;          // 영화 ID (내부 식별자)
    private String title;          // 영화 제목
    private String synopsis;       // 줄거리
    private String director;       // 감독
    private String genre;          // 장르
    private Integer runtimeMin;    // 상영 시간(분)
    private LocalDate releaseDate; // 개봉일
    private LocalDate endDate;     // 상영 종료일
    private String rating;         // 관람 등급 (ALL, 12, 15, 18 등)
    private String posterUrl;      // 포스터 이미지 URL
    private MovieStatus status;    // 영화 상태

    /**
     * Movie 엔티티 → Admin 영화 상세 응답 DTO 변환
     *
     * - Admin 상세 화면에 표시할 영화 정보를 구성하기 위한 변환 메서드
     * - 상태(status)를 포함하여 운영/관리 판단에 필요한 정보를 제공
     */
    public static MovieDetailAdminResponseDto from(Movie movie) {
        return MovieDetailAdminResponseDto.builder()
                .movieId(movie.getMovieId())
                .title(movie.getTitle())
                .synopsis(movie.getSynopsis())
                .director(movie.getDirector())
                .genre(movie.getGenre())
                .runtimeMin(movie.getRuntimeMin())
                .releaseDate(movie.getReleaseDate())
                .endDate(movie.getEndDate())
                .rating(movie.getRating())
                .posterUrl(movie.getPosterUrl())
                .status(movie.getStatus())
                .build();
    }
}
