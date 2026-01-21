package com.movieing.movieingbackend.movie.repository;

import com.movieing.movieingbackend.movie.entity.Movie;
import com.movieing.movieingbackend.movie.entity.MovieStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 영화(Movie) 엔티티 Repository
 *
 * - 영화 기본 CRUD 및 상태/날짜 기반 조회 쿼리 제공
 * - 사용자 화면 및 Admin 화면에서 사용하는 조회 로직 분리 가능
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    /**
     * 특정 상태이면서, 개봉일이 기준 날짜 이전(또는 동일)인 영화 조회
     *
     * - 예: NOW_SHOWING 또는 COMMING_SOON 중
     * - 오늘 날짜 기준으로 노출 가능한 영화 조회 시 사용
     */
    List<Movie> findByStatusAndReleaseDateLessThanEqual(MovieStatus status, LocalDate date);

    /**
     * 특정 상태이면서, 상영 종료일이 기준 날짜 이전인 영화 조회
     *
     * - 예: NOW_SHOWING 상태이지만 종료일이 지난 영화
     * - 상영 종료 처리(ENDED 전환) 대상 조회 시 사용
     */
    List<Movie> findByStatusAndEndDateLessThan(MovieStatus status, LocalDate date);

    /**
     * 특정 상태를 제외한 영화 목록 조회
     *
     * - Admin 목록 조회 시 DELETED 상태 제외 용도로 사용
     * - 물리 삭제 대신 소프트 삭제 정책을 적용하기 위한 조회 메서드
     */
    Page<Movie> findByStatusNot(MovieStatus status, Pageable pageable);

    /**
     * 특정 상태들을 포함한 영화 목록 조회
     * */
    List<Movie> findByStatusIn(List<MovieStatus> statuses);

    /**
     * 특정 상태를 제외한 영화 갯수 카운트
     * */
    Long countByStatusNot(MovieStatus status);

    /**
     * 특정 상태 영화 갯수 카운트
     * */
    Long countByStatus(MovieStatus status);

    /**
     * 상태 영화 갯수 가운트
     * */
    Long countByStatusIn(List<MovieStatus> statuses);

    /**
     * 상영중인 영화 중, 종료일 기준 7일 이내의 기간을 가진 영화 카운트
     * */
    @Query("""
        select count(m.movieId)
        from Movie m
        where m.status = :status
        and m.endDate is not null
        and m.endDate < :today
        and m.endDate <= :endDate
    """)
    Long countEndingSoonMovies(
            @Param("status") MovieStatus status,
            @Param("today") LocalDate today,
            @Param("endDate") LocalDate endDate
    );

    Page<Movie> findByStatus(MovieStatus status, Pageable pageable);

    @Query("""
        select m
        from Movie m
        where m.status <> :deleted
            and (
                lower(cast(m.title as string)) like concat('%', lower(:keywords), '%')
                or (:id is not null and m.movieId = :id)
                )
    """)
    Page<Movie> searchNotDeleted(
            @Param("deleted") MovieStatus deleted,
            @Param("keywords") String keywords,
            @Param("id") Long id,
            Pageable pageable);

    @Query("""
        select m
        from Movie m
        where m.status = :status
            and (
                lower(cast(m.title as string)) like concat('%', lower(:keywords), '%')
                or (:id is not null and m.movieId = :id) 
            )
    """)
    Page<Movie> searchByStatus(@Param("status") MovieStatus status,
                               @Param("keywords") String keywords,
                               @Param("id") Long id,
                               Pageable pageable);
}
