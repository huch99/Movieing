package com.movieing.movieingbackend.screen.entity;

/**
 * 상영관(Screen) 상태
 *
 * - 상영관의 생명주기 및 노출/운영 상태를 표현
 * - 물리 삭제 대신 DELETED 상태를 사용하여 소프트 삭제 처리
 *
 * 상태 흐름(권장):
 * DRAFT -> ACTIVE -> (HIDDEN) -> CLOSED -> DELETED
 */
public enum ScreenStatus {
    DRAFT,      // 작성 중 (초안, 운영 전 상태)

    ACTIVE,     // 운영 중 (상영 스케줄 등록 가능, 사용자 노출 가능)

    HIDDEN,     // 숨김 (운영 중이지만 사용자 노출 X, 관리자만 확인)

    CLOSED,     // 운영 종료 (더 이상 상영 스케줄 등록 불가)

    DELETED     // 삭제 (소프트 삭제, 모든 조회/수정 불가)
}
