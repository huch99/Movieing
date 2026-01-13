package com.movieing.movieingbackend.screen.repository;

import com.movieing.movieingbackend.screen.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 상영관(Screen) 엔티티 Repository
 *
 * - 상영관 기본 CRUD 기능 제공
 * - Admin / 운영 화면에서 사용하는 조회 로직의 기반
 * - 상태 기반/영화관 기준 조회 메서드는 추후 확장
 */
@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {
}
