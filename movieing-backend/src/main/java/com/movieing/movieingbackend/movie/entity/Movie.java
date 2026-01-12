package com.movieing.movieingbackend.movie.entity;

import com.movieing.movieingbackend.aspect.BaseTimeEntity;
import com.movieing.movieingbackend.common.exception.ConflictException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "movie")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    // 제목
    @Column(name = "title", nullable = true, length = 255)
    private String title;

    // 감독
    @Column(name = "director", nullable = true, length = 100)
    private String director;

    // 장르
    @Column(name = "genre", nullable = true, length = 50)
    private String genre;

    // 줄거리
    @Lob
    @Column(name = "synopsis", nullable = true)
    private String synopsis;

    // 상영 시간 (분)
    @Column(name = "runtime_min", nullable = true)
    private Integer runtimeMin;

    // 개봉일
    @Column(name = "release_date", nullable = true)
    private LocalDate releaseDate;

    // 상영 종료일
    @Column(name = "end_date", nullable = true)
    private LocalDate endDate;

    // 관랑등급 (예 : ALL, 12, 15, 18 등)
    @Column(name = "rating", nullable = true, length = 20)
    private String rating;

    // 포스터 URL
    @Column(name = "poster_url", nullable = true, length = 500)
    private String posterUrl;

    // 상태 기반 관리
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private MovieStatus status = MovieStatus.DRAFT;

    /* =========================
       도메인 메서드 (수정/삭제)
       ========================= */

    public void updateDetail(
            String title,
            String synopsis,
            String director,
            String genre,
            Integer runtimeMin,
            LocalDate releaseDate,
            LocalDate endDate,
            String rating,
            String posterUrl,
            MovieStatus status
    ) {
        if (title != null) this.title = title;
        if (synopsis != null) this.synopsis = synopsis;
        if (director != null) this.director = director;
        if (genre != null) this.genre = genre;
        if (runtimeMin != null) this.runtimeMin = runtimeMin;
        if (releaseDate != null) this.releaseDate = releaseDate;
        if (endDate != null) this.endDate = endDate;
        if (rating != null) this.rating = rating;
        if (posterUrl != null) this.posterUrl = posterUrl;
        if (status != null) this.status = status;
    }

    public void saveDraft() {
        this.status = MovieStatus.DRAFT;
    }

    public void complete() {
        if (this.status != MovieStatus.DRAFT) {
            throw new ConflictException("DRAFT 상태에서만 완료 처리할 수 있습니다.");
        }
        this.status = MovieStatus.COMING_SOON;
    }

    public void startShowing() {
        if (this.status != MovieStatus.COMING_SOON) {
            throw new ConflictException("COMING_SOON 상태에서만 상영 시작할 수 있습니다.");
        }
        this.status = MovieStatus.NOW_SHOWING;
    }

    public void endShowing() {
        if (this.status != MovieStatus.NOW_SHOWING) {
            throw new ConflictException("NOW_SHOWING 상태에서만 상영 종료할 수 있습니다.");
        }
        this.status = MovieStatus.ENDED;
    }

    public void hide() {
        this.status = MovieStatus.HIDDEN;
    }

    public void softDelete() {
        this.status = MovieStatus.DELETED;
    }

    public void setCommingSoon() {
        this.status = MovieStatus.COMING_SOON;
    }
}
