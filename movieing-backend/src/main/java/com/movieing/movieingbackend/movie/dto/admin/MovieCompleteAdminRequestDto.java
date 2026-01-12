package com.movieing.movieingbackend.movie.dto.admin;

import com.movieing.movieingbackend.movie.entity.Movie;
import com.movieing.movieingbackend.movie.entity.MovieStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieCompleteAdminRequestDto {

    private Long movieId;
    private String title;
    private String director;
    private String genre;
    private String synopsis;
    private Integer runtimeMin;
    private String rating;
    private String posterUrl;
    private LocalDate releaseDate;
    private LocalDate endDate;
    private MovieStatus status;

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
