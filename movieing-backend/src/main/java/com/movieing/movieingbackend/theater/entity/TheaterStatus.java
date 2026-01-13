package com.movieing.movieingbackend.theater.entity;

/**
 * 영화관(Theater) 상태 정의
 * <p>
 * - 영화관의 운영 상태 및 노출 여부를 표현하는 상태 값
 * - 관리자(Admin) 화면에서 상태 전이를 통해 영화관을 관리
 * <p>
 * 상태 설명:
 * - DRAFT   : 작성 중 상태 (초안, 사용자 노출 X)
 * - ACTIVE  : 운영 중 (사용자 노출 O, 예매 가능)
 * - HIDDEN  : 숨김 처리 (운영은 가능하나 사용자 노출 X)
 * - CLOSED  : 운영 종료 (정책에 따라 노출/예매 불가)
 * - DELETED : 소프트 삭제 (시스템에서만 관리, 사용자 노출 X)
 * <p>
 * 주의:
 * - 상태 전이 규칙은 엔티티가 아닌 서비스 레이어에서 관리하는 것을 권장
 */
public enum TheaterStatus {
    DRAFT,      // 작성 중
    ACTIVE,     // 운영중 (노출 + 예매 가능)
    HIDDEN,     // 숨김 (노출 X)
    CLOSED,     // 운영 종료 (정책에 따라 노출/예매 불가)
    DELETED     // 소프트 삭제
}
