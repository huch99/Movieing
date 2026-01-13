package com.movieing.movieingbackend.movie.entity;

/**
 * 영화 상태(MovieStatus)
 *
 * - 영화 엔티티의 생명주기 및 노출/예매 가능 여부를 제어
 * - Admin 영역에서 상태 전이를 통해 영화 운영 상태를 관리
 *
 * 기본 흐름:
 * DRAFT → COMING_SOON → NOW_SHOWING → ENDED
 *
 * 예외 흐름:
 * - 운영자 판단에 따라 HIDDEN 처리 가능
 * - 물리 삭제 대신 DELETED 상태로 소프트 삭제
 */
public enum MovieStatus {
    DRAFT,        // 작성 중(임시 저장 상태, 사용자 노출 X)

    COMING_SOON,  // 개봉 예정 (사용자 노출 O, 예매 가능 정책 적용 가능)

    NOW_SHOWING,  // 상영 중 (사용자 노출 O, 예매 가능)

    HIDDEN,       // 운영자가 숨김 처리 (사용자 노출 X)

    ENDED,        // 상영 종료 (예매 불가, 노출 여부는 정책에 따라 결정)

    DELETED       // 삭제 상태 (소프트 삭제, 모든 조회 대상에서 제외)
}
