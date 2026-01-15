package com.movieing.movieingbackend.movie.entity;

import com.movieing.movieingbackend.aspect.BaseTimeEntity;
import com.movieing.movieingbackend.common.exception.ConflictException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 영화(Movie) 엔티티
 * <p>
 * - 영화 예매 서비스에서 상영 대상이 되는 영화 정보를 관리
 * - Admin 영역에서 단계적 등록(DRAFT → COMMING_SOON → NOW_SHOWING → ENDED)을 지원
 * - 물리 삭제 대신 status 기반 상태 관리(Soft Delete) 적용
 * <p>
 * 상태 흐름:
 * DRAFT → COMMING_SOON → NOW_SHOWING → ENDED
 * ↓
 * HIDDEN
 * ↓
 * DELETED
 */
@Entity
@Getter
@Table(name = "movie", indexes = {
        @Index(name = "idx_movie_status", columnList = "status"),
        @Index(name = "idx_movie_release_date", columnList = "release_date"),
        @Index(name = "idx_movie_end_date", columnList = "end_date")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id", nullable = false)
    private Long movieId; // 내부 PK

    @Column(name = "title", nullable = true, length = 255)
    private String title; // 영화 제목

    @Column(name = "director", nullable = true, length = 100)
    private String director; // 감독

    @Column(name = "genre", nullable = true, length = 50)
    private String genre; // 장르

    @Lob
    @Column(name = "synopsis", nullable = true)
    private String synopsis; // 줄거리

    @Column(name = "runtime_min", nullable = true)
    private Integer runtimeMin; // 상영 시간(분)

    @Column(name = "release_date", nullable = true)
    private LocalDate releaseDate; // 개봉일

    @Column(name = "end_date", nullable = true)
    private LocalDate endDate; // 상영 종료일

    @Column(name = "rating", nullable = true, length = 20)
    private String rating; // 관람 등급 (ALL, 12, 15, 18 등)

    @Column(name = "poster_url", nullable = true, length = 500)
    private String posterUrl; // 포스터 이미지 URL

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private MovieStatus status = MovieStatus.DRAFT; // 영화 상태

    /* =========================
       도메인 메서드 (수정/상태 변경)
       ========================= */

    /**
     * 영화 상세 정보 수정
     * <p>
     * - null이 아닌 값만 선택적으로 반영
     * - Draft/운영 상태 모두에서 사용 가능
     */
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

    /**
     * 초안 상태로 저장
     */
    public void saveDraft() {
        this.status = MovieStatus.DRAFT;
    }

    /**
     * 영화 등록 완료 처리
     * <p>
     * - DRAFT 상태에서만 가능
     * - COMMING_SOON 상태로 전환
     */
    public void complete() {
        if (this.status != MovieStatus.DRAFT) {
            throw new ConflictException("DRAFT 상태에서만 완료 처리할 수 있습니다.");
        }
        this.status = MovieStatus.COMMING_SOON;
    }

    /**
     * 상영 시작 처리
     * <p>
     * - COMMING_SOON 상태에서만 가능
     */
    public void startShowing() {
        if (this.status != MovieStatus.COMMING_SOON) {
            throw new ConflictException("COMMING_SOON 상태에서만 상영 시작할 수 있습니다.");
        }
        this.status = MovieStatus.NOW_SHOWING;
    }

    /**
     * 상영 종료 처리
     * <p>
     * - NOW_SHOWING 상태에서만 가능
     */
    public void endShowing() {
        if (this.status != MovieStatus.NOW_SHOWING) {
            throw new ConflictException("NOW_SHOWING 상태에서만 상영 종료할 수 있습니다.");
        }
        this.status = MovieStatus.ENDED;
    }

    /**
     * 영화 숨김 처리
     * <p>
     * - 사용자 화면에서 노출되지 않음
     */
    public void hide() {
        this.status = MovieStatus.HIDDEN;
    }

    /**
     * 영화 소프트 삭제
     * <p>
     * - 물리 삭제 대신 DELETED 상태로 전환
     */
    public void softDelete() {
        this.status = MovieStatus.DELETED;
    }

    /**
     * 상영 예정 상태로 강제 전환
     * <p>
     * - 관리용 유틸 메서드
     * - 일반적인 흐름에서는 complete() 사용 권장
     */
    public void setCommingSoon() {
        this.status = MovieStatus.COMMING_SOON;
    }
}
