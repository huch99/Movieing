package com.movieing.movieingbackend.theater.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * 영화관 수정 요청 DTO (관리자)
 *
 * - 운영중(ACTIVE), 숨김(HIDDEN), 운영 종료(CLOSED) 상태의 영화관 정보를 수정할 때 사용
 * - 초안(DRAFT) 상태의 영화관은 수정 불가 (draft 저장 API 사용)
 * - 삭제(DELETED) 상태의 영화관은 수정 불가
 *
 * 수정 방식:
 * - 부분 수정(Partial Update)
 * - 모든 필드는 선택 입력이며, null 값은 "변경하지 않음"으로 처리
 *
 * 검증 책임:
 * - 필수 입력 여부: 없음 (모든 필드 선택 입력)
 * - 정책성 검증(위경도 쌍, 영업 시간 관계 등): 서비스 레이어
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TheaterUpdateAdminRequestDto {
    private String theaterName;
    private String address;
    private Double lat;
    private Double lng;
    private LocalTime openTime;
    private LocalTime closeTime;
}
