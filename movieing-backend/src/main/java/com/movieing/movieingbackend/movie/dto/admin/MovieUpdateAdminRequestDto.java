package com.movieing.movieingbackend.movie.dto.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieUpdateAdminRequestDto {

    @Size(max = 200, message = "제목은 200자 이하여야 합니다.")
    private String title;

    private String synopsis;
    private String director;
    private String genre;

    @Min(value = 1, message = "상영 시간은 1분 이상이어야 합니다.")
    private Integer runtimeMin;

    private LocalDate releaseDate;
    private LocalDate endDate;

    private String rating;
    private String posterUrl;
}
