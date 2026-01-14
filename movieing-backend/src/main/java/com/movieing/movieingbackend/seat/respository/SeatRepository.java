package com.movieing.movieingbackend.seat.respository;

import com.movieing.movieingbackend.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    // 특정 상영관의 좌석 개수
    long countByScreen_ScreenId(Long screenId);

    // 특정 상영관의 좌석 전체 삭제 (물리 삭제)
    void deleteByScreen_ScreenId(Long screenId);

    // 특정 상영관에 좌석 존재 여부
    boolean existsByScreen_ScreenId(Long screenId);

    /**
     * 특정 상영관(screen)에 속한 좌석 배치도 조회
     *
     * - row / col 기준으로 정렬
     * - 좌석 배치도 렌더링용
     */
    List<Seat> findByScreen_ScreenIdOrderBySeatRowAscSeatColAsc(Long screenId);

}
