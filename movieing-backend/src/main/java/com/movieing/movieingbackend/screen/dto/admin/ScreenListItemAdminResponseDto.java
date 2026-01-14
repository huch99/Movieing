package com.movieing.movieingbackend.screen.dto.admin;

import com.movieing.movieingbackend.screen.entity.Screen;
import com.movieing.movieingbackend.screen.entity.ScreenStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 상영관 목록 조회용 관리자 응답 DTO
 *
 * - 관리자 상영관 관리 화면 리스트에 사용
 * - 삭제(DELETED) 상태를 제외한 상영관 목록 정보를 표현
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreenListItemAdminResponseDto {
    private Long screenId;          // 상영관 ID
    private Long theaterId;         // 소속 영화관 ID
    private String screenName;      // 상영관 이름
    private Integer capacity;       // 수용 인원
    private ScreenStatus status;    // 상영관 상태

    public static ScreenListItemAdminResponseDto from(Screen screen) {
        return ScreenListItemAdminResponseDto.builder()
                .screenId(screen.getScreenId())
                .theaterId(screen.getTheater().getTheaterId())
                .screenName(screen.getScreenName())
                .capacity(screen.getCapacity())
                .status(screen.getStatus())
                .build();
    }
}
