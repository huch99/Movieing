package com.movieing.movieingbackend.screen.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 상영관 수정 요청 DTO (관리자)
 *
 * - 운영중(ACTIVE), 숨김(HIDDEN), 운영 종료(CLOSED) 상태의 상영관 정보를 수정할 때 사용
 * - 초안(DRAFT) 상태의 상영관은 수정 불가 (draft 저장 API 사용)
 * - 삭제(DELETED) 상태의 상영관은 수정 불가
 *
 * 수정 방식:
 * - 부분 수정(Partial Update)
 * - 모든 필드는 선택 입력이며, null 값은 "변경하지 않음"으로 처리
 *
 * 검증 책임:
 * - 필수 입력 여부: 없음 (모든 필드 선택 입력)
 * - 정책성 검증(수용 인원 범위, 영화관 존재 여부 등): 서비스 레이어
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreenUpdateAdminRequestDto {
    private Long theaterId;     // 소속 영화관 변경 (선택)

    private String screenName;  // 상영관 이름 변경 (선택)

    private Integer capacity;   // 수용 인원 변경 (선택, 0 이상 정책은 서비스에서 검증)
}
