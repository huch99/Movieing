package com.movieing.movieingbackend.screen.dto.admin;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 상영관 임시 저장(DRAFT) 요청 DTO (관리자)
 *
 * - 상영관 초안 상태에서 부분 저장을 위해 사용
 * - 모든 필드는 선택 입력이며, null 값은 "변경하지 않음"으로 처리
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreenDraftSaveAdminRequestDto {

    private Long theaterId;     // 소속 영화관 ID (선택)

    private String screenName;  // 상영관 이름 (선택)

    @Min(value = 0, message = "수용 인원은 0 이상이어야 합니다.")
    private Integer capacity;   // 수용 인원 (선택, 0 이상)

}
