package com.movieing.movieingbackend.movie.dto.admin;

import com.movieing.movieingbackend.movie.entity.Movie;
import com.movieing.movieingbackend.movie.entity.MovieStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 영화 등록 완료(Admin) 요청 DTO
 *
 * - Admin 영역에서 영화 정보를 "완성(complete)" 처리할 때 사용
 * - 초안(DRAFT) 상태에서 필수 정보가 모두 채워진 영화에 대해 최종 저장/상태 전환 목적
 * - 주로 수정 화면에서 기존 영화 정보를 로딩하거나,
 *   완료 처리 시 클라이언트 → 서버로 전달되는 데이터 모델로 사용됨
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieCompleteAdminRequestDto {

    @NotNull(message = "movieId는 필수입니다.")
    private Long movieId; // 영화 ID (내부 식별자)

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 255, message = "제목은 최대 255자까지 가능합니다.")
    private String title; // 영화 제목

    @NotBlank(message = "감독은 필수입니다.")
    @Size(max = 100, message = "감독은 최대 100자까지 가능합니다.")
    private String director; // 감독

    @NotBlank(message = "장르는 필수입니다.")
    @Size(max = 50, message = "장르는 최대 50자까지 가능합니다.")
    private String genre; // 장르

    @NotBlank(message = "줄거리는 필수입니다.")
    private String synopsis; // 줄거리 (Lob)

    @NotNull(message = "상영 시간은 필수입니다.")
    @Min(value = 1, message = "상영 시간은 1분 이상이어야 합니다.")
    @Max(value = 1000, message = "상영 시간은 1000분 이하여야 합니다.")
    private Integer runtimeMin; // 상영 시간(분)

    @NotBlank(message = "관람 등급은 필수입니다.")
    @Size(max = 20, message = "관람 등급은 최대 20자까지 가능합니다.")
    private String rating; // 관람 등급 (ALL, 12, 15, 18 등)

    @NotBlank(message = "포스터 URL은 필수입니다.")
    @Size(max = 500, message = "포스터 URL은 최대 500자까지 가능합니다.")
    private String posterUrl; // 포스터 이미지 URL

    @NotNull(message = "개봉일은 필수입니다.")
    private LocalDate releaseDate; // 개봉일

    @NotNull(message = "상영 종료일은 필수입니다.")
    private LocalDate endDate; // 상영 종료일

    @NotNull(message = "상태는 필수입니다.")
    private MovieStatus status; // 영화 상태

    /**
     * Movie 엔티티 → Admin 완료 요청 DTO 변환
     *
     * - 기존 영화 데이터를 Admin 완료/수정 화면에 전달하기 위한 변환 메서드
     * - 엔티티를 직접 노출하지 않고 DTO로 변환하여 계층 간 책임 분리
     */
    public static MovieCompleteAdminRequestDto from(Movie movie) {
        return MovieCompleteAdminRequestDto.builder()
                .movieId(movie.getMovieId())
                .title(movie.getTitle())
                .posterUrl(movie.getPosterUrl())
                .director(movie.getDirector())
                .genre(movie.getGenre())
                .synopsis(movie.getSynopsis())
                .runtimeMin(movie.getRuntimeMin())
                .rating(movie.getRating())
                .releaseDate(movie.getReleaseDate())
                .endDate(movie.getEndDate())
                .status(movie.getStatus())
                .build();
    }
}
