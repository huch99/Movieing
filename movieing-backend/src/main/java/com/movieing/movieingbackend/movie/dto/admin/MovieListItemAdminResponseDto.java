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
public class MovieListItemAdminResponseDto {

    private Long movieId;
    private String title;
    private String posterUrl;
    private LocalDate releaseDate;
    private LocalDate endDate;
    private MovieStatus status;

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
