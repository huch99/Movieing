package com.movieing.movieingbackend.screen.repository;

import com.movieing.movieingbackend.screen.entity.Screen;
import com.movieing.movieingbackend.screen.entity.ScreenStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 상영관(Screen) 엔티티 Repository
 *
 * - 상영관 기본 CRUD 기능 제공
 * - Admin / 운영 화면에서 사용하는 조회 로직의 기반
 * - 상태 기반/영화관 기준 조회 메서드는 추후 확장
 */
@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {
    Page<Screen> findByTheater_TheaterIdAndStatusNot(Long theaterId, ScreenStatus status, Pageable pageable);

    List<Screen> findByTheater_TheaterIdAndStatusIn(Long theaterId, List<ScreenStatus> statuses);

    Long countByStatusNot(ScreenStatus status);

    Long countByStatus(ScreenStatus status);

    @Query("""
        select s
        from Screen s
        where s.theater.theaterId = :theaterId
            and s.status <> :deleted
            and (
                :keywords is null
                or :keywords = ''
                or lower(s.screenName) like concat('%', lower(:keywords), '%')
                or (:id is not null and s.screenId = :id)
            )
    """)
    Page<Screen> searchNotDeleted(
            @Param("theaterId") Long theaterId,
            @Param("deleted") ScreenStatus deleted,
            @Param("keywords") String keywords,
            @Param("id") Long id,
            Pageable pageable
    );

    @Query("""
        select s
        from Screen s
        where s.theater.theaterId = :theaterId
            and s.status = :status
            and (
                :keywords is null
                or :keywords = ''
                or lower(s.screenName) like concat('%', lower(:keywords), '%')
                or (:id is not null and s.screenId = :id)
                )
    """)
    Page<Screen> searchByStatus(
            @Param("theaterId") Long theaterId,
            @Param("status") ScreenStatus status,
            @Param("keywords") String keywords,
            @Param("id") Long id,
            Pageable pageable
    );

    Page<Screen> findByTheater_TheaterIdAndStatus(Long theaterId, ScreenStatus status, Pageable pageable);
}
