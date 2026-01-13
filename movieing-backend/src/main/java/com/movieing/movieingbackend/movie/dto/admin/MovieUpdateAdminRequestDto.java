package com.movieing.movieingbackend.movie.dto.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 영화 정보 수정(Admin) 요청 DTO
 *
 * - Admin 영역에서 기존 영화 정보를 수정할 때 사용
 * - 모든 필드는 선택 사항(optional)이며, 전달된 값만 부분적으로 반영
 * - 상태(status) 변경은 전용 도메인 메서드에서 처리하고,
 *   본 DTO는 영화 상세 정보 수정에만 집중
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieUpdateAdminRequestDto {

    @Size(max = 200, message = "제목은 200자 이하여야 합니다.")
    private String title;          // 영화 제목

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
