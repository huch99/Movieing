package com.movieing.movieingbackend.user.repository;

import com.movieing.movieingbackend.user.entity.UserAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 관리자(UserAdmin) 엔티티용 Repository
 * <p>
 * - UserAdmin 엔티티에 대한 기본 CRUD 기능 제공
 * - User 엔티티와 PK를 공유하는(@MapsId) 관리자 전용 테이블 접근용
 * <p>
 * 주요 사용처:
 * - 관리자 계정 생성 시 UserAdmin 저장
 * - 관리자 상세 정보 조회 (소속 영화관 등)
 * - 특정 사용자 ID로 관리자 여부 판단
 */
@Repository
public interface UserAdminRepository extends JpaRepository<UserAdmin, Long> {
}
