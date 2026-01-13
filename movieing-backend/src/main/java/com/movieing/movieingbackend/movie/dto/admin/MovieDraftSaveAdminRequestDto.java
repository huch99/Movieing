package com.movieing.movieingbackend.movie.dto.admin;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * 영화 초안 저장(Admin) 요청 DTO
 *
 * - Admin 영역에서 영화 정보를 임시 저장(DRAFT)할 때 사용
 * - 모든 필드는 선택 사항(optional)이며, 입력된 값만 부분적으로 저장됨
 * - 필수값 검증은 "완료 처리(complete)" 단계에서 수행
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieDraftSaveAdminRequestDto {

    private String title;          // 영화 제목 (초안 단계에서는 필수 아님)
    private String synopsis;       // 줄거리
    private String director;       // 감독
    private String genre;          // 장르

    @Min(value = 1, message = "상영 시간은 1분 이상이어야 합니다.")
    private Integer runtimeMin;    // 상영 시간(분)

    private LocalDate releaseDate; // 개봉일
    private LocalDate endDate;     // 상영 종료일

    private String rating;         // 관람 등급 (ALL, 12, 15, 18 등)
    private String posterUrl;      // 포스터 이미지 URL
}
