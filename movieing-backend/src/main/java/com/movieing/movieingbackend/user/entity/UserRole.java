package com.movieing.movieingbackend.user.entity;

/**
 * 사용자 권한(Role) 정의
 *
 * - 인증(JWT) 및 인가(Spring Security)에서 사용하는 역할 구분 값
 * - SecurityConfig 및 JwtAuthFilter에서 ROLE_ 접두어와 함께 사용됨
 *
 * 권한 의미:
 * - USER    : 일반 사용자 (영화 조회, 예매 등)
 * - ADMIN   : 시스템 관리자 (영화관/영화/상영관 관리 등)
 * - THEATER : 영화관 관리자 (특정 영화관 운영자)
 */
public enum UserRole {
    USER,       // 일반 사용자
    ADMIN,      // 시스템 관리자
    THEATER     // 영화관 관리자
}
