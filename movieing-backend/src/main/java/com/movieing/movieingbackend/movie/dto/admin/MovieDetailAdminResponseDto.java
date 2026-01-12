package com.movieing.movieingbackend.movie.dto.admin;

import com.movieing.movieingbackend.movie.entity.Movie;
import com.movieing.movieingbackend.movie.entity.MovieStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class MovieDetailAdminResponseDto {

    private Long movieId;
    private String title;
    private String synopsis;
    private String director;
    private String genre;
    private Integer runtimeMin;
    private LocalDate releaseDate;
    private LocalDate endDate;
    private String rating;
    private String posterUrl;
    private MovieStatus status;

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
