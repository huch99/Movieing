package com.movieing.movieingbackend.theater.repository;

import com.movieing.movieingbackend.theater.entity.Theater;
import com.movieing.movieingbackend.theater.entity.TheaterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 영화관(Theater) 엔티티 Repository
 * <p>
 * - 영화관 정보에 대한 기본 CRUD 기능 제공
 * - 관리자(Admin) 화면에서 영화관 목록 조회 및 관리에 사용
 * <p>
 * 주요 사용처:
 * - 삭제(DELETED) 상태를 제외한 영화관 목록 조회
 * - 상태 기반 필터링을 통한 관리자 관리 화면 구성
 */
@Repository
public interface TheaterRepository extends JpaRepository<Theater, Long> {

    /**
     * 특정 상태를 제외한 영화관 목록 조회
     * <p>
     * - 주로 DELETED 상태를 제외하기 위해 사용
     *
     * @param status 제외할 영화관 상태
     * @return 상태가 status가 아닌 영화관 목록
     */
    List<Theater> findAllByStatusNot(TheaterStatus status);

    List<Theater> findByStatusIn(List<TheaterStatus> statuses);
}
