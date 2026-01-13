package com.movieing.movieingbackend.user.repository;

import com.movieing.movieingbackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자(User) 엔티티 Repository
 *
 * - 사용자 계정 조회 및 인증/인가 처리에 사용
 * - 이메일, publicUserId 기반 조회 메서드를 제공
 *
 * 주요 사용처:
 * - 로그인 시 이메일 기반 사용자 조회
 * - 회원가입 시 이메일 중복 체크
 * - 외부 노출용 식별자(publicUserId)를 통한 사용자 조회
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 사용자 조회
     */
    Optional<User> findByEmail(String email);

    /**
     * 이메일 중복 여부 확인
     */
    boolean existsByEmail(String email);

    /**
     * 외부 노출용 사용자 ID(publicUserId)로 사용자 조회
     */
    Optional<User> findByPublicUserId(String publicUserId);
}
